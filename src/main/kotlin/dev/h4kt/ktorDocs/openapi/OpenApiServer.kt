package dev.h4kt.ktorDocs.openapi

import kotlinx.serialization.Serializable

@Serializable
data class OpenApiServer(
    val url: String,
    val description: String = ""
)
