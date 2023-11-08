package dev.h4kt.ktorDocs.openapi.route

import dev.h4kt.ktorDocs.compat.SerialContentType
import dev.h4kt.ktorDocs.openapi.components.OpenApiSchema
import kotlinx.serialization.Serializable

@Serializable
data class OpenApiRouteBody(
    val description: String = "",
    val content: Map<SerialContentType, Schema> = emptyMap()
) {

    @Serializable
    data class Schema(
        val schema: OpenApiSchema
    )

}
