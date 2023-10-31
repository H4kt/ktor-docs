package dev.h4kt.ktorDocs.types

import dev.h4kt.ktorDocs.annotations.KtorDocsDsl
import dev.h4kt.ktorDocs.types.parameters.RouteParameters
import io.ktor.server.application.*
import io.ktor.util.pipeline.*

typealias CallHandler<TPathParams, TQueryParams> = suspend CallContext<TPathParams, TQueryParams>.() -> Unit

class Route<TPathParams : RouteParameters, TQueryParams : RouteParameters>(
    val path: String,
    val pathParametersBuilder: () -> TPathParams,
    val queryParametersBuilder: () -> TQueryParams,
    val handler: CallHandler<TPathParams, TQueryParams>
) {

    suspend fun handle(
        context: PipelineContext<Unit, ApplicationCall>
    ) {

        val pathParameters = pathParametersBuilder()
            .apply { parse(context.call.parameters) }

        val queryParameters = queryParametersBuilder()
            .apply { parse(context.call.request.queryParameters) }

        val callContext = CallContext(
            call = context.call,
            pathParameters = pathParameters,
            queryParameters = queryParameters
        )

        handler(callContext)

    }

}

class RouteBuilder<TPathParams : RouteParameters, TQueryParams : RouteParameters> {

    var description: String? = null
    var handler: CallHandler<TPathParams, TQueryParams> = {}

    @KtorDocsDsl
    fun handle(handler: CallHandler<TPathParams, TQueryParams>) {
        this.handler = handler
    }

}
