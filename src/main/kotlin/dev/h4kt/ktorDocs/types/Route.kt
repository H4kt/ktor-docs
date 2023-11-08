package dev.h4kt.ktorDocs.types

import dev.h4kt.ktorDocs.annotations.KtorDocsDsl
import dev.h4kt.ktorDocs.types.parameters.RouteParameters
import io.ktor.http.*
import io.ktor.util.reflect.*

typealias CallHandler<TPathParams, TQueryParams> = suspend CallContext<TPathParams, TQueryParams>.() -> Unit

data class DocumentedRoute(
    val method: HttpMethod,
    val description: String,
    val tags: List<String>,
    val pathParameters: RouteParameters,
    val queryParameters: RouteParameters,
    val requestBody: TypeInfo?,
    val responses: Map<HttpStatusCode, TypeInfo>
)

class RouteBuilder<TPathParams : RouteParameters, TQueryParams : RouteParameters> {

    var description: String = ""
    var tags = emptyList<String>()

    var requestBody: TypeInfo? = null

    internal var responsesBuilder = RouteResponsesBuilder()
    internal var handler: CallHandler<TPathParams, TQueryParams> = {}

    @KtorDocsDsl
    fun handle(handler: CallHandler<TPathParams, TQueryParams>) {
        this.handler = handler
    }

    @KtorDocsDsl
    fun responses(configure: RouteResponsesBuilder.() -> Unit) {
        responsesBuilder.configure()
    }

}

class RouteResponsesBuilder {

    internal val responses = mutableMapOf<HttpStatusCode, TypeInfo>()

    fun HttpStatusCode.returns(value: TypeInfo) {
        responses[this] = value
    }

    @KtorDocsDsl
    inline infix fun <reified T : Any> HttpStatusCode.returns(value: T) {
        this.returns(typeInfo<T>())
    }

}
