package dev.h4kt.ktorDocs.dsl

import dev.h4kt.ktorDocs.annotations.KtorDocsDsl
import dev.h4kt.ktorDocs.plugin.KtorDocs
import dev.h4kt.ktorDocs.types.CallContext
import dev.h4kt.ktorDocs.types.DocumentedRoute
import dev.h4kt.ktorDocs.types.RouteBuilder
import dev.h4kt.ktorDocs.types.parameters.RouteParameters
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*

@KtorDocsDsl
fun <TPathParams : RouteParameters, TQueryParams : RouteParameters> Route.get(
    path: String,
    pathParametersBuilder: () -> TPathParams,
    queryParametersBuilder: () -> TQueryParams,
    builder: RouteBuilder<TPathParams, TQueryParams>.() -> Unit
) {

    val settings = RouteBuilder<TPathParams, TQueryParams>()
        .apply(builder)

    val route = get(path) {

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

    val documentedRoute = DocumentedRoute(
        method = HttpMethod.Get,
        description = settings.description,
        tags = settings.tags,
        pathParameters = pathParametersBuilder(),
        queryParameters = queryParametersBuilder(),
        requestBody = settings.requestBody,
        responses = settings.responsesBuilder.responses
    )

}
