package dev.h4kt.ktorDocs.openapi

import dev.h4kt.ktorDocs.compat.SerialHttpMethod
import dev.h4kt.ktorDocs.openapi.components.OpenApiComponents
import dev.h4kt.ktorDocs.openapi.route.OpenApiRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenApiSpec(
    @SerialName("openapi") val version: String,
    val info: Info,
    val servers: List<OpenApiServer>,
    val tags: List<Tag>,
    val paths: Map<String, Map<SerialHttpMethod, OpenApiRoute>> = emptyMap(),
    val components: OpenApiComponents? = null
) {

    @Serializable
    data class Info(
        val title: String,
        val version: String,
        val description: String = ""
    )

    @Serializable
    data class Tag(
        val name: String,
        val description: String = ""
    )

}
