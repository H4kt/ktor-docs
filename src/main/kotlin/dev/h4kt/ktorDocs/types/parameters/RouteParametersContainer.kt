package dev.h4kt.ktorDocs.types.parameters

import dev.h4kt.ktorDocs.types.parameters.impl.IntRouteParameter
import dev.h4kt.ktorDocs.types.parameters.impl.LongRouteParameter
import dev.h4kt.ktorDocs.types.parameters.impl.StringRouteParameter
import dev.h4kt.ktorDocs.types.parameters.impl.UUIDRouteParameter
import io.ktor.http.*
import java.util.UUID

class RouteParametersContainer : Iterable<RouteParameter<*>> {

    private val parameters = mutableListOf<RouteParameter<*>>()

    fun string(
        configure: RouteParameterBuilder.() -> Unit
    ): RouteParameter<String> {
        val settings = RouteParameterBuilder().apply(configure)
        return StringRouteParameter(
            name = settings.name,
            description = settings.description,
            optional = false
        ).also(::add)
    }

    fun int(
        configure: RouteParameterBuilder.() -> Unit
    ): RouteParameter<Int> {
        val settings = RouteParameterBuilder().apply(configure)
        return IntRouteParameter(
            name = settings.name,
            description = settings.description,
            optional = false
        ).also(::add)
    }

    fun long(
        configure: RouteParameterBuilder.() -> Unit
    ): RouteParameter<Long> {
        val settings = RouteParameterBuilder().apply(configure)
        return LongRouteParameter(
            name = settings.name,
            description = settings.description,
            optional = false
        ).also(::add)
    }

    fun uuid(
        configure: RouteParameterBuilder.() -> Unit
    ): RouteParameter<UUID> {
        val settings = RouteParameterBuilder().apply(configure)
        return UUIDRouteParameter(
            name = settings.name,
            description = settings.description,
            optional = false
        ).also(::add)
    }

    fun add(parameter: RouteParameter<*>) {
        parameters += parameter
    }

    fun parse(parameters: Parameters) {
        this.parameters.forEach {
            it.parse(parameters)
        }
    }

    override fun iterator(): Iterator<RouteParameter<*>> {
        return parameters.iterator()
    }

}
