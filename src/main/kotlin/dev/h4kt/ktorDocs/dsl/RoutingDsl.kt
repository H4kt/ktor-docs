package dev.h4kt.ktorDocs.dsl

import dev.h4kt.ktorDocs.annotations.KtorDocsDsl
import dev.h4kt.ktorDocs.types.CallContext
import dev.h4kt.ktorDocs.types.RouteBuilder
import dev.h4kt.ktorDocs.types.parameters.RouteParameters
import io.ktor.server.application.*
import io.ktor.server.routing.*

@KtorDocsDsl
fun <TPathParams : RouteParameters, TQueryParams : RouteParameters> Route.get(
    path: String,
    pathParametersBuilder: () -> TPathParams,
    queryParametersBuilder: () -> TQueryParams,
    builder: RouteBuilder<TPathParams, TQueryParams>.() -> Unit
) {

    val settings = RouteBuilder<TPathParams, TQueryParams>()
        .apply(builder)

    val originalRoute = get(path) {

        val pathParameters = pathParametersBuilder()
            .apply { parse(call.parameters) }

        val queryParameters = queryParametersBuilder()
            .apply { parse(call.request.queryParameters) }

        val callContext = CallContext(
            call = call,
            pathParameters = pathParameters,
            queryParameters = queryParameters
        )

        settings.handler(callContext)

    }

}
