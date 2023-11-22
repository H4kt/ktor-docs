package dev.h4kt.ktorDocs

import dev.h4kt.ktorDocs.types.DocumentedRoute
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRoute
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRouteBody
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRouteParameter
import dev.h4kt.ktorDocs.types.parameters.RouteParameter
import io.ktor.http.*

internal fun DocumentedRoute.toOpenApiRoute(
    authentications: Set<String>
): OpenApiRoute {

    val parameters = mutableListOf<OpenApiRouteParameter>()

    this.parameters.path.forEach {
        parameters += it.toOpenApiRouteParameter(OpenApiRouteParameter.Type.PATH)
    }

    this.parameters.query.forEach {
        parameters += it.toOpenApiRouteParameter(OpenApiRouteParameter.Type.QUERY)
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

            val content = if (value.type == Unit::class) {
                emptyMap()
            } else {
                mapOf(
                    contentType to OpenApiRouteBody.Schema(
                        schema = value.kotlinType!!.toOpenApiSchema()
                    )
                )
            }

            OpenApiRouteBody(
                content = content
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

private fun RouteParameter<*>.toOpenApiRouteParameter(
    type: OpenApiRouteParameter.Type
): OpenApiRouteParameter {
    return OpenApiRouteParameter(
        type = type,
        name = name,
        schema = typeInfo.kotlinType!!.toOpenApiSchema(),
        required = required
    )
}
