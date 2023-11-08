package dev.h4kt.ktorDocs.plugin

import dev.h4kt.ktorDocs.openapi.components.OpenApiSchema
import dev.h4kt.ktorDocs.openapi.main
import dev.h4kt.ktorDocs.openapi.route.OpenApiRoute
import dev.h4kt.ktorDocs.types.DocumentedRoute
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import java.io.File
import kotlin.reflect.full.memberProperties

class KtorDocsConfig {

    var swaggerUIPath: String = "swagger"
    var swaggerConfig: SwaggerConfig = SwaggerConfig()

    var documentationFilePath: String = "documentation.yml"

    internal val routes = mutableMapOf<String, List<Unit>>()

}

val KtorDocs = createApplicationPlugin(
    name = "KtorDocs",
    createConfiguration = ::KtorDocsConfig
) {

    on(MonitoringEvent(ApplicationStarted)) { application ->

        main()

        application.routing {
            swaggerUI("swagger", File("documentation.yml"))
        }

    }

}

//private fun DocumentedRoute.toOpenApiRoute(): OpenApiRoute {
//    return OpenApiRoute()
//}
//
//private val builtInTypes = listOf(
//    String::class,
//    Char::class,
//    Int::class,
//    Short::class,
//    Byte::class,
//    Long::class,
//    Boolean::class,
//)
//
//private fun TypeInfo.toOpenApiSchema(): OpenApiSchema {
//
//    type.typeParameters
//    when (type) {
//    }
//
//}

data class Foo(
    val value: List<String>
)

fun main() {

    val (type) = typeInfo<Foo>()
    val valueProperty = type.memberProperties.first()

    println(valueProperty.returnType)

}
