package dev.h4kt.ktorDocs.plugin

import com.charleskorn.kaml.*
import dev.h4kt.ktorDocs.extensions.documentation
import dev.h4kt.ktorDocs.extensions.isDocumented
import dev.h4kt.ktorDocs.toOpenApiSchema
import dev.h4kt.ktorDocs.types.DocumentedRoute
import dev.h4kt.ktorDocs.types.openapi.OpenApiServer
import dev.h4kt.ktorDocs.types.openapi.OpenApiSpec
import dev.h4kt.ktorDocs.types.openapi.OpenApiSpecPaths
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRoute
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRouteBody
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRouteParameter
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

private val logger = LoggerFactory.getLogger("KtorDocsPlugin")

class KtorDocsConfig {

    class Swagger {
        var isEnabled = true
        var path = "swagger"
        var config: SwaggerConfig.() -> Unit = {}
    }

    class OpenApi {

        var version = "3.0.0"

        var info = OpenApiSpec.Info(
            title = "Default title",
            version = "v1",
            description = ""
        )

        var servers = listOf<OpenApiServer>()

    }

    var swagger = Swagger()
    var openApi = OpenApi()
    var documentationFilePath: String = "documentation.yml"

}

val KtorDocs = createApplicationPlugin(
    name = "KtorDocs",
    createConfiguration = ::KtorDocsConfig
) {

    val config = pluginConfig

    on(MonitoringEvent(ApplicationStarted)) { application ->

        logger.info("Gathering documented routes...")

        val routes = measureTimedValue {
            application.gatherDocumentedRoutes()
        }

        logger.info("Gathered ${routes.value.size} documented routes in ${routes.duration}")


        logger.info("Generating OpenAPI spec...")

        val openApiSpecGenerationTime = measureTime {
            generateOpenApiSpec(
                config = config.openApi,
                outputFilePath = config.documentationFilePath,
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
    config: KtorDocsConfig.OpenApi,
    outputFilePath: String,
    routes: Map<String, Map<HttpMethod, Route>>
) {

    val tags = mutableSetOf<String>()

    val paths: OpenApiSpecPaths = routes.mapValues { (_, value) ->
        value.mapValues mapRoutes@{ (_, route) ->

            val documentation = route.documentation
            tags.addAll(documentation.tags)

            return@mapRoutes documentation.toOpenApiRoute()
        }
    }

    val spec = OpenApiSpec(
        version = config.version,
        info = config.info,
        servers = config.servers,
        paths = paths
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

private fun DocumentedRoute.toOpenApiRoute(): OpenApiRoute {

    val parameters = mutableListOf<OpenApiRouteParameter>()

    this.parameters.path.forEach {
        parameters += OpenApiRouteParameter(
            type = OpenApiRouteParameter.Type.PATH,
            name = it.name,
            schema = it.type.kotlinType!!.toOpenApiSchema()
        )
    }

    this.parameters.query.forEach {
        parameters += OpenApiRouteParameter(
            type = OpenApiRouteParameter.Type.QUERY,
            name = it.name,
            schema = it.type.kotlinType!!.toOpenApiSchema()
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
        parameters = parameters,
        requestBody = requestBody,
        responses = responses
    )
}

private fun Application.gatherDocumentedRoutes(): Map<String, Map<HttpMethod, Route>> {

    val result = mutableMapOf<String, MutableMap<HttpMethod, Route>>()

    traverseChildren(
        currentRoute = routing {},
        currentPath = "",
        result = result
    )

    return result
}

private fun traverseChildren(
    currentRoute: Route,
    currentPath: String,
    result: MutableMap<String, MutableMap<HttpMethod, Route>>
) {

    val selector = currentRoute.selector
    if (selector is HttpMethodRouteSelector) {

        if (!currentRoute.isDocumented) {
            return // No need to generate docs for this route
        }

        val path = currentPath.takeIf { it.isNotEmpty() }
            ?: "/"

        result.computeIfAbsent(path) { mutableMapOf() }
            .set(selector.method, currentRoute)

        return
    }

    val suffix = when (selector) {
        TrailingSlashRouteSelector,
        is HttpHeaderRouteSelector,
        is HttpAcceptRouteSelector,
        is HttpMultiAcceptRouteSelector -> ""
        else -> selector.toString()
            .takeIf { it.isNotEmpty() }
            ?.let { "/$it" }
            ?: ""
    }

    currentRoute.children.forEach {
        traverseChildren(it, "$currentPath$suffix", result)
    }

}
