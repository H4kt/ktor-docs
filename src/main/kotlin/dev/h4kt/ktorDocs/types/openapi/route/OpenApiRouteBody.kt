package dev.h4kt.ktorDocs.types.openapi.route

import dev.h4kt.ktorDocs.serializers.SerialContentType
import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
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
