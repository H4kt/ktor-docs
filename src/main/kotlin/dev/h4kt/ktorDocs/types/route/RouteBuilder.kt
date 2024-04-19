package dev.h4kt.ktorDocs.types.route

import dev.h4kt.ktorDocs.annotations.KtorDocsDsl
import dev.h4kt.ktorDocs.types.CallContext
import dev.h4kt.ktorDocs.types.parameters.RouteParameters
import io.ktor.util.reflect.*

typealias CallHandler<TParams> = suspend CallContext<TParams>.() -> Unit

class RouteBuilder<TParams : RouteParameters> {

    var description: String = ""
    var tags = emptyList<String>()

    var requestBody: TypeInfo? = null

    internal var responsesBuilder = RouteResponsesBuilder()
    internal var handler: CallHandler<TParams> = {}

    @KtorDocsDsl
    fun handle(handler: CallHandler<TParams>) {
        this.handler = handler
    }

    @KtorDocsDsl
    fun responses(configure: RouteResponsesBuilder.() -> Unit) {
        responsesBuilder.configure()
    }

}
