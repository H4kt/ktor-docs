package dev.h4kt.ktorDocs.types.parameters

import dev.h4kt.ktorDocs.annotations.KtorDocsDsl
import io.ktor.server.application.*

abstract class RouteParameters {

    internal object Empty : RouteParameters()

    companion object {
        internal val emptyDelegate = { Empty }
    }

    @KtorDocsDsl
    val path = RouteParametersContainer()

    @KtorDocsDsl
    val query = RouteParametersContainer()

    internal fun parse(
        call: ApplicationCall
    ) {
        path.parse(call.parameters)
        query.parse(call.request.queryParameters)
    }

}
