package dev.h4kt.ktorDocs.types.openapi.components

import kotlinx.serialization.Serializable

@Serializable
data class OpenApiComponents(
    val securitySchemes: Map<String, OpenApiSecurityScheme> = emptyMap(),
    val schemas: Map<String, OpenApiSchema> = emptyMap()
)
