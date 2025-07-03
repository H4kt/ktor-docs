package dev.h4kt.ktorDocs._v2.types.route.response

import dev.h4kt.ktorDocs.annotations.KtorDocsDsl
import io.ktor.http.HttpStatusCode
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class RouteResponsesBuilder {

    val nothing = typeOf<Unit>()

    internal val responses = mutableMapOf<HttpStatusCode, ResponseBody>()

    @KtorDocsDsl
    infix fun HttpStatusCode.returns(value: KType) {
        responses[this] = ResponseBody.Schema(type = value)
    }

    @KtorDocsDsl
    infix fun HttpStatusCode.returns(configure: SchemaResponseBuilder.() -> Unit) {
        val settings = SchemaResponseBuilder().apply(configure)
        requireNotNull(settings.body)
        responses[this] = ResponseBody.Schema(
            type = settings.body,
            description = settings.description
        )
    }

}
