package dev.h4kt.ktorDocs.types

import dev.h4kt.ktorDocs.annotations.KtorDocsDsl
import dev.h4kt.ktorDocs.types.parameters.RouteParameters
import io.ktor.http.*
import io.ktor.util.reflect.*
import kotlin.reflect.KClass

typealias CallHandler<TParams> = suspend CallContext<TParams>.() -> Unit

data class DocumentedRoute(
    val description: String,
    val tags: List<String>,
    val parameters: RouteParameters,
    val requestBody: TypeInfo?,
    val responses: Map<HttpStatusCode, TypeInfo>
)

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

class RouteResponsesBuilder {

    val nothing = typeInfo<Unit>()

    internal val responses = mutableMapOf<HttpStatusCode, TypeInfo>()

    @KtorDocsDsl
    infix fun HttpStatusCode.returns(value: TypeInfo) {
        responses[this] = value
    }

    @KtorDocsDsl
    inline infix fun <reified T : Any> HttpStatusCode.returns(value: KClass<T>) {
        this returns typeInfo<T>()
    }

    @KtorDocsDsl
    fun noContent() {
        HttpStatusCode.NoContent returns typeInfo<Unit>()
    }

}
