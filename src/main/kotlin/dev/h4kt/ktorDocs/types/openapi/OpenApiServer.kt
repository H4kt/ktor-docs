package dev.h4kt.ktorDocs.types.openapi

import kotlinx.serialization.Serializable

@Serializable
data class OpenApiServer(
    var url: String,
    var description: String = ""
)
