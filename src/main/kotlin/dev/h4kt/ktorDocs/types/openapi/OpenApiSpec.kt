package dev.h4kt.ktorDocs.types.openapi

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.SingleLineStringStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import dev.h4kt.ktorDocs.compat.SerialHttpMethod
import dev.h4kt.ktorDocs.types.openapi.components.OpenApiComponents
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRoute
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
