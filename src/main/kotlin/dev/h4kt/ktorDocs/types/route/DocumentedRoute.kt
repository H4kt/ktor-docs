package dev.h4kt.ktorDocs.types.route

import dev.h4kt.ktorDocs.types.parameters.RouteParameters
import io.ktor.http.*
import io.ktor.util.reflect.*

data class DocumentedRoute(
    val description: String,
    val tags: List<String>,
    val parameters: RouteParameters,
    val requestBody: TypeInfo?,
    val responses: Map<HttpStatusCode, RouteResponse>
)
