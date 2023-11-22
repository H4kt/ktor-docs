package dev.h4kt.ktorDocs.types.openapi.route

import dev.h4kt.ktorDocs.serializers.SerialContentType
import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class OpenApiRouteBody constructor(
    @EncodeDefault val description: String = "",
    val content: Map<SerialContentType, Schema> = emptyMap()
) {

    @Serializable
    data class Schema(
        val schema: OpenApiSchema
    )

}
