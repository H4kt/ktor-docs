package dev.h4kt.ktorDocs

import dev.h4kt.ktorDocs.plugin.TypeMap
import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRoute
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRouteBody
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRouteParameter
import dev.h4kt.ktorDocs.types.parameters.RouteParameter
import dev.h4kt.ktorDocs.types.route.DocumentedRoute
import dev.h4kt.ktorDocs.utils.ktype.isList
import io.ktor.http.ContentType
import kotlin.reflect.KType

internal fun DocumentedRoute.toOpenApiRoute(
    authentications: Set<String>,
    typeMap: TypeMap
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
        ?.let { kType ->
            OpenApiRouteBody(
                content = mapOf(
                    contentType to OpenApiRouteBody.Schema(kType.toReference(typeMap))
                )
            )
        }

    val responses = responses
        .mapKeys { (key) -> key.value.toString() }
        .mapValues { (_, value) ->

            val content = if (value.body.type == Unit::class) {
                emptyMap()
            } else {
                mapOf(
                    contentType to OpenApiRouteBody.Schema(
                        schema = value.body.kotlinType!!.toReference(typeMap)
                    )
                )
            }

            OpenApiRouteBody(
                content = content,
                description = value.description ?: ""
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

private fun KType.toReference(
    typeMap: TypeMap
): OpenApiSchema {

    val schema = when {
        isList -> OpenApiSchema.Array(
            items = typeMap.getTypeReferenceOrRegister(arguments.first().type!!),
            nullable = isMarkedNullable
        )
        else -> typeMap.getTypeReferenceOrRegister(this)
    }

    return schema
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
