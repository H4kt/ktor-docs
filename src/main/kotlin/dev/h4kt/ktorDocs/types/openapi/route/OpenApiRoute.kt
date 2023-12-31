package dev.h4kt.ktorDocs.types.openapi.route

import kotlinx.serialization.Serializable

@Serializable
data class OpenApiRoute(
    val tags: List<String> = emptyList(),
    val summary: String = "",
    val parameters: List<OpenApiRouteParameter> = emptyList(),
    val security: List<Map<String, List<String>>> = emptyList(),
    val requestBody: OpenApiRouteBody? = null,
    val responses: Map<String, OpenApiRouteBody>
)
