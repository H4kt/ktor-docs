package dev.h4kt.ktorDocs.plugin

import dev.h4kt.ktorDocs.types.DocumentedRoute
import dev.h4kt.ktorDocs.types.openapi.main
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import io.ktor.util.*
import java.io.File

private val RouteDocumentationAttributeKey = AttributeKey<DocumentedRoute>("documentation")

class KtorDocsConfig {

    var swaggerUIPath: String = "swagger"
    var swaggerConfig: SwaggerConfig = SwaggerConfig()

    var documentationFilePath: String = "documentation.yml"

}

val KtorDocs = createApplicationPlugin(
    name = "KtorDocs",
    createConfiguration = ::KtorDocsConfig
) {

    on(MonitoringEvent(ApplicationStarted)) { application ->

        val routing = application.routing {}

        routing.selector

        main()

        application.routing {
            swaggerUI("swagger", File("documentation.yml"))
        }

    }

}
