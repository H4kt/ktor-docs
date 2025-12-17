package dev.h4kt.ktorDocs.plugin

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.SingleLineStringStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.charleskorn.kaml.encodeToStream
import dev.h4kt.ktorDocs.annotations.UnsafeAPI
import dev.h4kt.ktorDocs.generation.converters.CollectionTypeConverter
import dev.h4kt.ktorDocs.generation.converters.DataClassTypeConverter
import dev.h4kt.ktorDocs.generation.converters.EnumTypeConverter
import dev.h4kt.ktorDocs.generation.converters.JavaUuidTypeConverter
import dev.h4kt.ktorDocs.generation.converters.KotlinUuidTypeConverter
import dev.h4kt.ktorDocs.generation.converters.KotlinXDateTimeTypeConverter
import dev.h4kt.ktorDocs.generation.converters.PrimitiveTypeConverter
import dev.h4kt.ktorDocs.generation.converters.SealedTypeConverter
import dev.h4kt.ktorDocs.generation.converters.TypeConverter
import dev.h4kt.ktorDocs.extensions.documentation
import dev.h4kt.ktorDocs.extensions.isDocumented
import dev.h4kt.ktorDocs.generation.SchemaGenerator
import dev.h4kt.ktorDocs.generation.converters.KotlinTimeTypeConverter
import dev.h4kt.ktorDocs.toOpenApiRoute
import dev.h4kt.ktorDocs.toOpenApiSecurityScheme
import dev.h4kt.ktorDocs.types.openapi.OpenApiServer
import dev.h4kt.ktorDocs.types.openapi.OpenApiSpec
import dev.h4kt.ktorDocs.types.openapi.OpenApiSpecPaths
import dev.h4kt.ktorDocs.types.openapi.components.OpenApiComponents
import dev.h4kt.ktorDocs.utils.getInternalField
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
import io.ktor.server.plugins.swagger.SwaggerConfig
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.HttpAcceptRouteSelector
import io.ktor.server.routing.HttpHeaderRouteSelector
import io.ktor.server.routing.HttpMethodRouteSelector
import io.ktor.server.routing.HttpMultiAcceptRouteSelector
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingNode
import io.ktor.server.routing.TrailingSlashRouteSelector
import io.ktor.server.routing.routing
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

        internal var config: SwaggerConfig.() -> Unit = {}

        fun config(configure: SwaggerConfig.() -> Unit) {
            config = configure
        }

    }

    class OpenApi {

        var version = "3.0.0"

        val info = OpenApiSpec.Info(
            title = "Default title",
            version = "v1",
            description = ""
        )

        val servers = mutableListOf<OpenApiServer>()

        fun info(configure: OpenApiSpec.Info.() -> Unit) {
            info.apply(configure)
        }

        fun server(configure: OpenApiServer.() -> Unit) {
            servers += OpenApiServer("").apply(configure)
        }

    }

    val swagger = Swagger()
    val openApi = OpenApi()

    var documentationFilePath: String = "documentation.yml"

    internal val typeConverters = mutableListOf(
        CollectionTypeConverter(),
        DataClassTypeConverter(),
        EnumTypeConverter(),
        JavaUuidTypeConverter(),
        KotlinTimeTypeConverter(),
        KotlinUuidTypeConverter(),
        KotlinXDateTimeTypeConverter(),
        PrimitiveTypeConverter(),
        SealedTypeConverter()
    )

    fun swagger(configure: Swagger.() -> Unit) {
        swagger.apply(configure)
    }

    fun openApi(configure: OpenApi.() -> Unit) {
        openApi.apply(configure)
    }

    fun typeConverters(vararg typeConverters: TypeConverter) {
        this.typeConverters.addAll(typeConverters)
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
            application.generateOpenApiSpec(
                typeConverters = config.typeConverters,
                config = config.openApi,
                outputFilePath = config.documentationFilePath,
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

private fun Application.generateOpenApiSpec(
    typeConverters: List<TypeConverter>,
    config: KtorDocsConfig.OpenApi,
    outputFilePath: String,
    authenticationProviders: Map<String, AuthenticationProvider>,
    routes: Map<String, Map<HttpMethod, RouteWithAuthentications>>
) {

    val schemaGenerator = SchemaGenerator(typeConverters)

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

    File(outputFilePath).outputStream().use {
        yaml.encodeToStream(spec, it)
    }

}

@OptIn(UnsafeAPI::class)
private fun Application.gatherAuthenticationProviders(): Map<String, AuthenticationProvider> {

    val authenticationPlugin = pluginOrNull(Authentication)
        ?: return emptyMap()

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
