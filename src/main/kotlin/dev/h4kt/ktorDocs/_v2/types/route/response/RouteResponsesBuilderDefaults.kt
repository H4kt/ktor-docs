package dev.h4kt.ktorDocs._v2.types.route.response

import dev.h4kt.ktorDocs.annotations.KtorDocsDsl
import io.ktor.http.HttpStatusCode

@KtorDocsDsl
fun RouteResponsesBuilder.noContent(
    configure: SchemaResponseBuilder.() -> Unit
) {
    val settings = SchemaResponseBuilder().apply(configure)
    HttpStatusCode.NoContent returns {
        body = nothing
        description = settings.description
    }
}
