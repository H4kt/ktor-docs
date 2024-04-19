package dev.h4kt.ktorDocs.types.route

import dev.h4kt.ktorDocs.annotations.KtorDocsDsl
import io.ktor.http.*
import io.ktor.util.reflect.*
import kotlin.reflect.KClass

class RouteResponsesBuilder {

    val nothing = typeInfo<Unit>()

    internal val responses = mutableMapOf<HttpStatusCode, RouteResponse>()

    @KtorDocsDsl
    infix fun HttpStatusCode.returns(value: TypeInfo) {
        responses[this] = RouteResponse(value)
    }

    @KtorDocsDsl
    inline infix fun <reified T : Any> HttpStatusCode.returns(value: KClass<T>) {
        this returns typeInfo<T>()
    }

    @KtorDocsDsl
    infix fun HttpStatusCode.returns(configure: RouteResponse.() -> Unit) {
        responses[this] = RouteResponse(nothing).apply(configure)
    }

    @KtorDocsDsl
    fun noContent() {
        HttpStatusCode.NoContent returns typeInfo<Unit>()
    }

}
