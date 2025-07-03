package dev.h4kt.ktorDocs.plugin

import dev.h4kt.ktorDocs.types.openapi.OpenApiServer
import dev.h4kt.ktorDocs.types.openapi.OpenApiSpec
import io.ktor.server.plugins.swagger.SwaggerConfig

class KtorDocsPlugin {

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

    fun swagger(configure: Swagger.() -> Unit) {
        swagger.apply(configure)
    }

    fun openApi(configure: OpenApi.() -> Unit) {
        openApi.apply(configure)
    }

}
