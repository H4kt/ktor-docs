package dev.h4kt.ktorDocs.types.parameters

import dev.h4kt.ktorDocs.types.parameters.impl.StringRouteParameter
import io.ktor.http.*

abstract class RouteParameters {

    protected val parameters = mutableListOf<RouteParameter<*>>()

    protected fun string(
        configure: RouteParameterBuilder.() -> Unit
    ): RouteParameter<String> {
        val settings = RouteParameterBuilder().apply(configure)
        return StringRouteParameter(
            name = settings.name,
            description = settings.description,
            optional = false
        )
    }

    /*protected fun optionalString(
        builder: RouteParameterBuilder.() -> Unit
    ): RouteParameter<String?> {
        TODO()
    }*/

    internal fun parse(
        parameters: Parameters
    ) {
        this.parameters.forEach { it.parse(parameters) }
    }

}
