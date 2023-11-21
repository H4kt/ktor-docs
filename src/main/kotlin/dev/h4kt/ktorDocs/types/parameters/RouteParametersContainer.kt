package dev.h4kt.ktorDocs.types.parameters

import dev.h4kt.ktorDocs.types.parameters.impl.*
import io.ktor.http.*
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import java.util.UUID
import kotlin.reflect.KFunction
import kotlin.time.Duration

class RouteParametersContainer : Iterable<RouteParameter<*>> {

    private val parameters = mutableListOf<RouteParameter<*>>()

    fun string(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, ::StringRouteParameter)

    fun int(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, ::IntRouteParameter)

    fun long(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, ::LongRouteParameter)

    fun uuid(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, ::UUIDRouteParameter)

    fun duration(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, ::DurationRouteParameter)

    fun date(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, ::LocalDateRouteParameter)

    fun datetime(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, ::LocalDateTimeRouteParameter)

    fun instant(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, ::InstantRouteParameter)

    private fun <T : Any> parameter(
        configure: RouteParameterBuilder.() -> Unit,
        construct: (String, String, Boolean) -> RouteParameter<T>
    ): RouteParameter<T> {
        val settings = RouteParameterBuilder().apply(configure)
        return construct(settings.name, settings.description, false)
            .also(::add)
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
