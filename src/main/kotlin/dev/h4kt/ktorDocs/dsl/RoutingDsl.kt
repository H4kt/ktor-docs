package dev.h4kt.ktorDocs.dsl

import dev.h4kt.ktorDocs.annotations.KtorDocsDsl
import dev.h4kt.ktorDocs.extensions.documentation
import dev.h4kt.ktorDocs.plugin.KtorDocs
import dev.h4kt.ktorDocs.types.CallContext
import dev.h4kt.ktorDocs.types.DocumentedRoute
import dev.h4kt.ktorDocs.types.RouteBuilder
import dev.h4kt.ktorDocs.types.parameters.RouteParameters
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*

/* --- GET start --- */
@KtorDocsDsl
fun <TParams : RouteParameters> Route.get(
    path: String,
    parametersBuilder: () -> TParams,
    builder: RouteBuilder<TParams>.() -> Unit
) = method(path, HttpMethod.Get, parametersBuilder, builder)

@KtorDocsDsl
fun Route.get(
    path: String,
    builder: RouteBuilder<RouteParameters>.() -> Unit
) = method(path, HttpMethod.Get, RouteParameters.emptyDelegate, builder)

@KtorDocsDsl
fun <TParams : RouteParameters> Route.get(
    parametersBuilder: () -> TParams,
    builder: RouteBuilder<TParams>.() -> Unit
) = method("/", HttpMethod.Get, parametersBuilder, builder)

@KtorDocsDsl
fun Route.get(
    builder: RouteBuilder<RouteParameters>.() -> Unit
) = method("/", HttpMethod.Get, RouteParameters.emptyDelegate, builder)
/* --- GET end --- */

/* --- POST start --- */
@KtorDocsDsl
fun <TParams : RouteParameters> Route.post(
    path: String,
    parametersBuilder: () -> TParams,
    builder: RouteBuilder<TParams>.() -> Unit
) = method(path, HttpMethod.Post, parametersBuilder, builder)

@KtorDocsDsl
fun Route.post(
    path: String,
    builder: RouteBuilder<RouteParameters>.() -> Unit
) = method(path, HttpMethod.Post, RouteParameters.emptyDelegate, builder)

@KtorDocsDsl
fun <TParams : RouteParameters> Route.post(
    parametersBuilder: () -> TParams,
    builder: RouteBuilder<TParams>.() -> Unit
) = method("/", HttpMethod.Post, parametersBuilder, builder)

@KtorDocsDsl
fun Route.post(
    builder: RouteBuilder<RouteParameters>.() -> Unit
) = method("/", HttpMethod.Post, RouteParameters.emptyDelegate, builder)
/* --- POST end --- */

/* --- PATCH start --- */
@KtorDocsDsl
fun <TParams : RouteParameters> Route.patch(
    path: String,
    parametersBuilder: () -> TParams,
    builder: RouteBuilder<TParams>.() -> Unit
) = method(path, HttpMethod.Patch, parametersBuilder, builder)

@KtorDocsDsl
fun Route.patch(
    path: String,
    builder: RouteBuilder<RouteParameters>.() -> Unit
) = method(path, HttpMethod.Patch, RouteParameters.emptyDelegate, builder)

@KtorDocsDsl
fun <TParams : RouteParameters> Route.patch(
    parametersBuilder: () -> TParams,
    builder: RouteBuilder<TParams>.() -> Unit
) = method("/", HttpMethod.Patch, parametersBuilder, builder)

@KtorDocsDsl
fun Route.patch(
    builder: RouteBuilder<RouteParameters>.() -> Unit
) = method("/", HttpMethod.Patch, RouteParameters.emptyDelegate, builder)
/* --- PATCH end --- */

/* --- PUT start --- */
@KtorDocsDsl
fun <TParams : RouteParameters> Route.put(
    path: String,
    parametersBuilder: () -> TParams,
    builder: RouteBuilder<TParams>.() -> Unit
) = method(path, HttpMethod.Put, parametersBuilder, builder)

@KtorDocsDsl
fun Route.put(
    path: String,
    builder: RouteBuilder<RouteParameters>.() -> Unit
) = method(path, HttpMethod.Put, RouteParameters.emptyDelegate, builder)

@KtorDocsDsl
fun <TParams : RouteParameters> Route.put(
    parametersBuilder: () -> TParams,
    builder: RouteBuilder<TParams>.() -> Unit
) = method("/", HttpMethod.Put, parametersBuilder, builder)

@KtorDocsDsl
fun Route.put(
    builder: RouteBuilder<RouteParameters>.() -> Unit
) = method("/", HttpMethod.Put, RouteParameters.emptyDelegate, builder)
/* --- PUT end --- */

/* --- DELETE start --- */
@KtorDocsDsl
fun <TParams : RouteParameters> Route.delete(
    path: String,
    parametersBuilder: () -> TParams,
    builder: RouteBuilder<TParams>.() -> Unit
) = method(path, HttpMethod.Delete, parametersBuilder, builder)

@KtorDocsDsl
fun Route.delete(
    path: String,
    builder: RouteBuilder<RouteParameters>.() -> Unit
) = method(path, HttpMethod.Delete, RouteParameters.emptyDelegate, builder)

@KtorDocsDsl
fun <TParams : RouteParameters> Route.delete(
    parametersBuilder: () -> TParams,
    builder: RouteBuilder<TParams>.() -> Unit
) = method("/", HttpMethod.Delete, parametersBuilder, builder)

@KtorDocsDsl
fun Route.delete(
    builder: RouteBuilder<RouteParameters>.() -> Unit
) = method("/", HttpMethod.Delete, RouteParameters.emptyDelegate, builder)
/* --- DELETE end --- */

/* --- HEAD start --- */
@KtorDocsDsl
fun <TParams : RouteParameters> Route.head(
    path: String,
    parametersBuilder: () -> TParams,
    builder: RouteBuilder<TParams>.() -> Unit
) = method(path, HttpMethod.Head, parametersBuilder, builder)

@KtorDocsDsl
fun Route.head(
    path: String,
    builder: RouteBuilder<RouteParameters>.() -> Unit
) = method(path, HttpMethod.Head, RouteParameters.emptyDelegate, builder)

@KtorDocsDsl
fun <TParams : RouteParameters> Route.head(
    parametersBuilder: () -> TParams,
    builder: RouteBuilder<TParams>.() -> Unit
) = method("/", HttpMethod.Head, parametersBuilder, builder)

@KtorDocsDsl
fun Route.head(
    builder: RouteBuilder<RouteParameters>.() -> Unit
) = method("/", HttpMethod.Head, RouteParameters.emptyDelegate, builder)
/* --- HEAD end --- */

/* --- OPTIONS start --- */
@KtorDocsDsl
fun <TParams : RouteParameters> Route.options(
    path: String,
    parametersBuilder: () -> TParams,
    builder: RouteBuilder<TParams>.() -> Unit
) = method(path, HttpMethod.Options, parametersBuilder, builder)

@KtorDocsDsl
fun Route.options(
    path: String,
    builder: RouteBuilder<RouteParameters>.() -> Unit
) = method(path, HttpMethod.Options, RouteParameters.emptyDelegate, builder)

@KtorDocsDsl
fun <TParams : RouteParameters> Route.options(
    parametersBuilder: () -> TParams,
    builder: RouteBuilder<TParams>.() -> Unit
) = method("/", HttpMethod.Options, parametersBuilder, builder)

@KtorDocsDsl
fun Route.options(
    builder: RouteBuilder<RouteParameters>.() -> Unit
) = method("/", HttpMethod.Options, RouteParameters.emptyDelegate, builder)
/* --- OPTIONS end --- */

@KtorDocsDsl
fun <TParams : RouteParameters> Route.method(
    path: String,
    method: HttpMethod,
    parametersBuilder: () -> TParams,
    builder: RouteBuilder<TParams>.() -> Unit
) {

    val settings = RouteBuilder<TParams>()
        .apply(builder)

    val handler = settings.handler

    val route = this@method.route(path, method) {
        handle {

            val parameters = parametersBuilder()
                .apply { parse(call) }

            val callContext = CallContext(
                call = call,
                parameters = parameters
            )

            handler(callContext)

        }
    }

    val documentedRoute = DocumentedRoute(
        description = settings.description,
        tags = settings.tags,
        parameters = parametersBuilder(),
        requestBody = settings.requestBody,
        responses = settings.responsesBuilder.responses
    )

    route.documentation = documentedRoute

}
