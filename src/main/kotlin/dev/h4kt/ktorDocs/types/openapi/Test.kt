package dev.h4kt.ktorDocs.types.openapi

import com.charleskorn.kaml.*
import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRoute
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRouteBody
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRouteParameter
import io.ktor.http.*
import java.io.File

fun main() {

    val spec = OpenApiSpec(
        version = "3.0.0",
        info = OpenApiSpec.Info(
            title = "Test API docs",
            version = "v1",
            description = "Dummy description"
        ),
        servers = listOf(
            OpenApiServer(
                url = "http://localhost:1337",
                description = "Local test server"
            )
        ),
        tags = listOf(
            OpenApiSpec.Tag("common")
        ),
        paths = mapOf(
            "/foo/bar" to mapOf(
                HttpMethod.Get to OpenApiRoute(
                    tags = listOf("common"),
                    summary = "Test API route",
                    parameters = listOf(
                        OpenApiRouteParameter(
                            type = OpenApiRouteParameter.Type.QUERY,
                            name = "query",
                            schema = OpenApiSchema.String()
                        )
                    ),
                    responses = mapOf(
                        "200" to OpenApiRouteBody(
                            description = "Success",
                            content = mapOf(
                                ContentType.Application.Json to OpenApiRouteBody.Schema(
                                    schema = OpenApiSchema.Object(
                                        properties = mapOf(
                                            "query" to OpenApiSchema.String()
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
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

    val file = File("documentation.yml")

    file.outputStream().use {
        yaml.encodeToStream(spec, it)
    }

}
