package dev.h4kt.ktorDocs._v2.types.route

import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RoutingNode

class DocumentedRoutingNode(
    parent: RoutingNode?,
    selector: RouteSelector,
    developmentMode: Boolean = false,
    environment: ApplicationEnvironment,
    val documentation: RouteDocumentation
) : RoutingNode(
    parent = parent,
    selector = selector,
    developmentMode = developmentMode,
    environment = environment
)
