package dev.h4kt.ktorDocs.types.route

import io.ktor.util.reflect.*

data class RouteResponse(
    var body: TypeInfo,
    var description: String? = null
)
