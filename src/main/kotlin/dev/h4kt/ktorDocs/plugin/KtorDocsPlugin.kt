package dev.h4kt.ktorDocs.plugin

import com.charleskorn.kaml.*
import dev.h4kt.ktorDocs.annotations.UnsafeAPI
import dev.h4kt.ktorDocs.extensions.documentation
import dev.h4kt.ktorDocs.extensions.isDocumented
import dev.h4kt.ktorDocs.toOpenApiSchema
import dev.h4kt.ktorDocs.toOpenApiSecurityScheme
import dev.h4kt.ktorDocs.types.DocumentedRoute
import dev.h4kt.ktorDocs.types.openapi.OpenApiServer
import dev.h4kt.ktorDocs.types.openapi.OpenApiSpec
import dev.h4kt.ktorDocs.types.openapi.OpenApiSpecPaths
import dev.h4kt.ktorDocs.types.openapi.components.OpenApiComponents
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRoute
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRouteBody
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRouteParameter
import dev.h4kt.ktorDocs.utils.getInternalField
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

private typealias RouteWithAuthentications = Pair<Set<String>, Route>

private val logger = LoggerFactory.getLogger("KtorDocsPlugin")

class KtorDocsConfig {

    class Swagger {
        var isEnabled = true
        var path = "swagger"
        var config: SwaggerConfig.() -> Unit = {}
    }

    class OpenApi {

        var version = "3.0.0"

        val info = OpenApiSpec.Info(
            title = "Default title",
            version = "v1",
            description = ""
        )

        var servers = listOf<OpenApiServer>()

        fun info(configure: OpenApiSpec.Info.() -> Unit) {
            info.apply(configure)
        }

    }

    val swagger = Swagger()
    val openApi = OpenApi()

    var documentationFilePath: String = "documentation.yml"

    fun swagger(configure: Swagger.() -> Unit) {
        swagger.apply(configure)
    }

    fun openApi(configure: OpenApi.() -> Unit) {
        openApi.apply(configure)
    }

}

val KtorDocs = createApplicationPlugin(
    name = "KtorDocs",
    createConfiguration = ::KtorDocsConfig
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
            try {
                application.generateOpenApiSpec(
                    config = config.openApi,
                    outputFilePath = config.documentationFilePath,
                    authenticationProviders = authenticationProviders.value,
                    routes = routes.value
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
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

private fun Application.generateOpenApiSpec(
    config: KtorDocsConfig.OpenApi,
    outputFilePath: String,
    authenticationProviders: Map<String, AuthenticationProvider>,
    routes: Map<String, Map<HttpMethod, RouteWithAuthentications>>
) {

    val tags = mutableSetOf<String>()

    val paths: OpenApiSpecPaths = routes.mapValues { (_, value) ->
        value.mapValues mapRoutes@{ (_, routeWithAuthentications) ->

            val (authentications, route) = routeWithAuthentications

            val documentation = route.documentation
            tags.addAll(documentation.tags)

            return@mapRoutes documentation.toOpenApiRoute(authentications)
        }
    }

    val securitySchemes = authenticationProviders
        .mapValues { (_, value) ->
            value.toOpenApiSecurityScheme(this)
        }
        .filterValues {
            it != null
        }
        .mapValues { (_, value) ->
            value!!
        }

    val spec = OpenApiSpec(
        version = config.version,
        info = config.info,
        servers = config.servers,
        paths = paths,
        components = OpenApiComponents(
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

    File(outputFilePath).outputStream().use {
        yaml.encodeToStream(spec, it)
    }

}

private fun DocumentedRoute.toOpenApiRoute(
    authentications: Set<String>
): OpenApiRoute {

    val parameters = mutableListOf<OpenApiRouteParameter>()

    this.parameters.path.forEach {
        parameters += OpenApiRouteParameter(
            type = OpenApiRouteParameter.Type.PATH,
            name = it.name,
            schema = it.typeInfo.kotlinType!!.toOpenApiSchema()
        )
    }

    this.parameters.query.forEach {
        parameters += OpenApiRouteParameter(
            type = OpenApiRouteParameter.Type.QUERY,
            name = it.name,
            schema = it.typeInfo.kotlinType!!.toOpenApiSchema()
        )
    }

    // TODO: gather content type info
    val contentType = ContentType.Application.Json

    val requestBody = requestBody
        ?.kotlinType
        ?.toOpenApiSchema()
        ?.let {
            OpenApiRouteBody(
                content = mapOf(
                    contentType to OpenApiRouteBody.Schema(
                        schema = it
                    )
                )
            )
        }

    val responses = responses
        .mapKeys { (key) -> key.value.toString() }
        .mapValues { (_, value) ->
            OpenApiRouteBody(
                content = mapOf(
                    contentType to OpenApiRouteBody.Schema(
                        schema = value.kotlinType!!.toOpenApiSchema()
                    )
                )
            )
        }

    return OpenApiRoute(
        tags = tags,
        summary = description,
        security = authentications.map { mapOf(it to emptyList()) },
        parameters = parameters,
        requestBody = requestBody,
        responses = responses
    )
}

@OptIn(UnsafeAPI::class)
private fun Application.gatherAuthenticationProviders(): Map<String, AuthenticationProvider> {

    val authenticationPlugin = plugin(Authentication)

    val config = authenticationPlugin.getInternalField<AuthenticationConfig>("config")
    val providers = config.getInternalField<Map<String?, AuthenticationProvider>>("providers")

    return providers.filterKeys { it != null }
        .mapKeys { (key) ->
            key!!
        }
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
    currentRoute: Route,
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
