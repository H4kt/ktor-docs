package dev.h4kt.ktorDocs.plugin

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.SingleLineStringStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.charleskorn.kaml.encodeToStream
import dev.h4kt.ktorDocs.annotations.UnsafeAPI
import dev.h4kt.ktorDocs.extensions.documentation
import dev.h4kt.ktorDocs.extensions.isDocumented
import dev.h4kt.ktorDocs.extensions.nameOrDefault
import dev.h4kt.ktorDocs.generation.SchemaGenerator
import dev.h4kt.ktorDocs.generation.results.SchemaGenerationResult
import dev.h4kt.ktorDocs.types.openapi.OpenApiSpec
import dev.h4kt.ktorDocs.types.openapi.OpenApiSpecPaths
import dev.h4kt.ktorDocs.types.openapi.components.OpenApiComponents
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRoute
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRouteBody
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRouteParameter
import dev.h4kt.ktorDocs.types.route.DocumentedRoute
import dev.h4kt.ktorDocs.utils.getInternalField
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import io.ktor.server.application.pluginOrNull
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.AuthenticationProvider
import io.ktor.server.auth.AuthenticationRouteSelector
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.HttpAcceptRouteSelector
import io.ktor.server.routing.HttpHeaderRouteSelector
import io.ktor.server.routing.HttpMethodRouteSelector
import io.ktor.server.routing.HttpMultiAcceptRouteSelector
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingNode
import io.ktor.server.routing.TrailingSlashRouteSelector
import io.ktor.server.routing.routing
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

private typealias RouteWithAuthentications = Pair<Set<String>, Route>

private val logger = LoggerFactory.getLogger("KtorDocsPlugin")

val KtorDocs = createApplicationPlugin(
    name = "KtorDocs",
    createConfiguration = ::KtorDocsPluginConfig
) {

    val config = pluginConfig

    on(MonitoringEvent(ApplicationStarted)) { application ->

        logger.info("Gathering authentication providers...")

        val authenticationProviders = measureTimedValue {
            application.gatherAuthenticationProviders()
        }

        logger.info("Gathered ${authenticationProviders.value.size} authentication providers in ${authenticationProviders.duration}")

        logger.info("Gathering documented routes...")

        val routes = measureTimedValue {
            application.gatherDocumentedRoutes()
        }

        logger.info("Gathered ${routes.value.size} documented routes in ${routes.duration}")

        logger.info("Generating OpenAPI spec...")

        val openApiSpecGenerationTime = measureTime {
            generateOpenApiSpec(
                application = application,
                config = config,
                authenticationProviders = authenticationProviders.value,
                routes = routes.value
            )
        }

        logger.info("OpenAPI spec generation took $openApiSpecGenerationTime")

        if (config.swagger.isEnabled) {

            application.routing {
                swaggerUI(
                    path = config.swagger.path,
                    apiFile = File(config.documentationFilePath),
                    block = config.swagger.config
                )
            }

        }

    }

}

private fun generateOpenApiSpec(
    application: Application,
    config: KtorDocsPluginConfig,
    authenticationProviders: Collection<AuthenticationProvider>,
    routes: Map<String, Map<HttpMethod, RouteWithAuthentications>>
) {

    val schemaGenerator = SchemaGenerator(config.typeConverters)

    val tags = mutableSetOf<String>()

    val paths: OpenApiSpecPaths = routes.mapValues { (path, value) ->
        value.mapValues mapRoutes@{ (method, routeWithAuthentications) ->

            val (authentications, route) = routeWithAuthentications

            val documentation = route.documentation
            tags.addAll(documentation.tags)

            return@mapRoutes documentation.toOpenApiRoute(
                logger,
                schemaGenerator,
                method,
                path,
                authentications
            )
        }
    }

    val securitySchemes = authenticationProviders
        .mapNotNull { provider ->
            val converter = config.authProviderConverters
                .firstOrNull { it.canConvert(provider) }

            if (converter == null) {
                logger.warn("Failed to convert auth provider ${provider.nameOrDefault}: no converter found")
                return@mapNotNull null
            }

            return@mapNotNull try {
                provider.nameOrDefault to converter.convert(
                    provider = provider,
                    application = application
                )
            } catch (ex: Exception) {
                logger.warn("Failed to convert auth provider ${provider.nameOrDefault}", ex)
                null
            }
        }
        .toMap()

    val spec = OpenApiSpec(
        version = "3.1.0",
        info = config.openApi.info,
        servers = config.openApi.servers,
        paths = paths,
        components = OpenApiComponents(
            schemas = schemaGenerator.generate(),
            securitySchemes = securitySchemes.takeIf { it.isNotEmpty() } ?: emptyMap()
        )
    )

    val yaml = Yaml(
        configuration = YamlConfiguration(
            encodeDefaults = false,
            polymorphismStyle = PolymorphismStyle.Property,
            singleLineStringStyle = SingleLineStringStyle.Plain,
            sequenceBlockIndent = 2
        )
    )

    File(config.documentationFilePath).outputStream().use {
        yaml.encodeToStream(spec, it)
    }

}

private fun DocumentedRoute.toOpenApiRoute(
    logger: Logger,
    schemaGenerator: SchemaGenerator,
    method: HttpMethod,
    path: String,
    authentications: Set<String>
): OpenApiRoute {

    val logTarget = "$method $path"

    val parameters = mutableListOf<OpenApiRouteParameter>()

    this.parameters.path.forEach { parameter ->
        val kotlinType = parameter.typeInfo.kotlinType
        if (kotlinType == null) {
            logger.warn("Failed to generate path parameter ${parameter.name} schema for $logTarget: no kotlin type found")
            return@forEach
        }

        val schemaResult = schemaGenerator.generateSchema(parameter.typeInfo.kotlinType!!)
        if (schemaResult !is SchemaGenerationResult.Success) {
            logger.warn("Failed to generate path parameter ${parameter.name} schema for $logTarget: $schemaResult")
            return@forEach
        }

        parameters += OpenApiRouteParameter(
            type = OpenApiRouteParameter.Type.PATH,
            name = parameter.name,
            schema = schemaResult.schema,
            required = parameter.required
        )
    }

    this.parameters.query.forEach { parameter ->
        val kotlinType = parameter.typeInfo.kotlinType
        if (kotlinType == null) {
            logger.warn("Failed to generate query parameter ${parameter.name} schema for $logTarget: no kotlin type found")
            return@forEach
        }

        val schemaResult = schemaGenerator.generateSchema(parameter.typeInfo.kotlinType!!)
        if (schemaResult !is SchemaGenerationResult.Success) {
            logger.warn("Failed to generate query parameter ${parameter.name} schema for $logTarget: $schemaResult")
            return@forEach
        }

        parameters += OpenApiRouteParameter(
            type = OpenApiRouteParameter.Type.QUERY,
            name = parameter.name,
            schema = schemaResult.schema,
            required = parameter.required
        )
    }

    // TODO: gather content type info
    val contentType = ContentType.Application.Json

    val requestBody = requestBody?.let {
        val kotlinType = it.kotlinType
        if (kotlinType == null) {
            logger.warn("Failed to generate request body schema for $logTarget: no kotlin type found")
            return@let null
        }

        val schemaResult = schemaGenerator.generateSchema(kotlinType)
        if (schemaResult !is SchemaGenerationResult.Success) {
            logger.warn("Failed to generate request body schema for $logTarget: $schemaResult")
            return@let null
        }

        return@let OpenApiRouteBody(
            content = mapOf(
                contentType to OpenApiRouteBody.Schema(
                    schema = schemaResult.schema
                )
            )
        )
    }

    val mappedResponses = mutableMapOf<String, OpenApiRouteBody>()

    responses.forEach { (statusCode, response) ->
        val bodyKotlinType = response.body.kotlinType
        if (bodyKotlinType == null) {
            logger.warn("Failed to generate response body schema for $logTarget -> $statusCode: no kotlin type found")
            return@forEach
        }

        val content = if (response.body.type == Unit::class) {
            emptyMap()
        } else {
            val schemaResult = schemaGenerator.generateSchema(bodyKotlinType)
            if (schemaResult !is SchemaGenerationResult.Success) {
                logger.warn("Failed to generate response body schema for $logTarget -> $statusCode: $schemaResult")
                return@forEach
            }

            mapOf(
                contentType to OpenApiRouteBody.Schema(
                    schema = schemaResult.schema
                )
            )
        }

        mappedResponses[statusCode.value.toString()] = OpenApiRouteBody(
            content = content,
            description = response.description ?: ""
        )
    }

    return OpenApiRoute(
        tags = tags,
        summary = description,
        security = authentications.map { mapOf(it to emptyList()) },
        parameters = parameters,
        requestBody = requestBody,
        responses = mappedResponses
    )
}

@OptIn(UnsafeAPI::class)
private fun Application.gatherAuthenticationProviders(): Collection<AuthenticationProvider> {

    val authenticationPlugin = pluginOrNull(Authentication)
        ?: return emptyList()

    val config = authenticationPlugin.getInternalField<AuthenticationConfig>("config")
    val providers = config.getInternalField<Map<String?, AuthenticationProvider>>("providers")

    return providers.values
}

private fun Application.gatherDocumentedRoutes(): Map<String, Map<HttpMethod, RouteWithAuthentications>> {

    val result = mutableMapOf<String, MutableMap<HttpMethod, RouteWithAuthentications>>()

    traverseChildren(
        currentRoute = routing {},
        currentPath = "",
        authentications = emptySet(),
        result = result
    )

    return result
}

private fun traverseChildren(
    currentRoute: RoutingNode,
    currentPath: String,
    authentications: Set<String>,
    result: MutableMap<String, MutableMap<HttpMethod, RouteWithAuthentications>>
) {

    val selector = currentRoute.selector
    if (selector is HttpMethodRouteSelector) {

        if (!currentRoute.isDocumented) {
            return // No need to generate docs for this route
        }

        val path = currentPath.takeIf { it.isNotEmpty() }
            ?: "/"

        result.computeIfAbsent(path) { mutableMapOf() }
            .set(selector.method, authentications to currentRoute)

        return
    }

    val downstreamAuthentications = when (selector) {
        is AuthenticationRouteSelector ->
            authentications + selector.names.filterNotNull()
        else -> authentications
    }

    val suffix = when (selector) {
        TrailingSlashRouteSelector,
        is HttpHeaderRouteSelector,
        is HttpAcceptRouteSelector,
        is AuthenticationRouteSelector,
        is HttpMultiAcceptRouteSelector -> ""
        else -> selector.toString()
            .takeIf { it.isNotEmpty() }
            ?.let { "/$it" }
            ?: ""
    }

    currentRoute.children.forEach {
        traverseChildren(
            currentRoute = it,
            currentPath = "$currentPath$suffix",
            authentications = downstreamAuthentications,
            result = result
        )
    }

}
