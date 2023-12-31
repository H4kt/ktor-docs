package dev.h4kt.ktorDocs.types.openapi.route

import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenApiRouteParameter(
    @SerialName("in") val type: Type,
    val name: String,
    val schema: OpenApiSchema,
    val required: Boolean = false
) {

    enum class Type {
        @SerialName("header") HEADER,
        @SerialName("path") PATH,
        @SerialName("query") QUERY
    }

}
