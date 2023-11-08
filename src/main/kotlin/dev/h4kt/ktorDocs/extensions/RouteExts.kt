package dev.h4kt.ktorDocs.extensions

import io.ktor.server.routing.*

internal val Route.absolutePath: String
    get() {

        val parentPath = parent?.absolutePath ?: "/"

        return when (selector) {
            is TrailingSlashRouteSelector,
//            is AuthenticationRouteSelector,
            is HttpMethodRouteSelector,
            is HttpHeaderRouteSelector,
            is HttpAcceptRouteSelector -> parentPath
            else -> parentPath.let { if (it.endsWith("/")) it else "$it/" } + selector.toString()
        }
    }
