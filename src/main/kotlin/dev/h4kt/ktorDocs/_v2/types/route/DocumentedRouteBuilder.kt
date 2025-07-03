package dev.h4kt.ktorDocs._v2.types.route

import dev.h4kt.ktorDocs._v2.types.parameters.RouteParameters
import dev.h4kt.ktorDocs.annotations.KtorDocsDsl
import dev.h4kt.ktorDocs.types.route.CallHandler
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class DocumentedRouteBuilder<TParams : RouteParameters> {

    @KtorDocsDsl
    var description: String = ""

    @KtorDocsDsl
    internal var tags: Array<out String> = arrayOf()

    internal var requestBody: RequestBody? = null
    internal var handler: CallHandler<TParams> = {}

    @KtorDocsDsl
    fun tags(vararg tags: String) {
        this.tags = tags
    }

    @KtorDocsDsl
    fun requestBody(type: KType) {
        requestBody = RequestBody.Schema(type)
    }

    @KtorDocsDsl
    inline fun <reified T> requestBody() = requestBody(typeOf<T>())

    @KtorDocsDsl
    fun handle(block: CallHandler<TParams>) {
        handler = block
    }

}
