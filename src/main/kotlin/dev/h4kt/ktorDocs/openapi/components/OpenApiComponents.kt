package dev.h4kt.ktorDocs.openapi.components

import kotlinx.serialization.Serializable

@Serializable
data class OpenApiComponents(
    val securitySchemes: Map<String, OpenApiSecurityScheme>,
    val schemas: List<Unit>
)
