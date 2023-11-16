package dev.h4kt.ktorDocs.types.openapi

import dev.h4kt.ktorDocs.serializers.SerialHttpMethod
import dev.h4kt.ktorDocs.types.openapi.components.OpenApiComponents
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias OpenApiSpecPaths = Map<String, Map<SerialHttpMethod, OpenApiRoute>>

@Serializable
data class OpenApiSpec(
    @SerialName("openapi") val version: String,
    val info: Info,
    val servers: List<OpenApiServer> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val paths: OpenApiSpecPaths = emptyMap(),
    val components: OpenApiComponents? = null
) {

    @Serializable
    data class Info(
        var title: String,
        var version: String,
        var description: String = ""
    )

    @Serializable
    data class Tag(
        val name: String,
        val description: String = ""
    )

}
