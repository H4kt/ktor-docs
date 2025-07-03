package dev.h4kt.ktorDocs._v2.types.route

import dev.h4kt.ktorDocs._v2.types.parameters.RouteParameters
import dev.h4kt.ktorDocs._v2.types.route.response.ResponseBody
import io.ktor.http.HttpStatusCode

data class RouteDocumentation(
    val description: String,
    val tags: Array<out String>,
    val parameters: RouteParameters,
    val requestBody: RequestBody,
    val responses: Map<HttpStatusCode, ResponseBody>
)
