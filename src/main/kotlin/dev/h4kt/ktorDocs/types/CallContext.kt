package dev.h4kt.ktorDocs.types

import dev.h4kt.ktorDocs.types.parameters.RouteParameters
import io.ktor.server.application.*

data class CallContext<TPathParams : RouteParameters, TQueryParams : RouteParameters>(
    val call: ApplicationCall,
    val pathParameters: TPathParams,
    val queryParameters: TQueryParams
)
