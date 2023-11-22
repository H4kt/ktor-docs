package dev.h4kt.ktorDocs.types.parameters

import dev.h4kt.ktorDocs.annotations.KtorDocsDsl
import dev.h4kt.ktorDocs.types.parameters.parsers.EnumParameterParser
import dev.h4kt.ktorDocs.types.parameters.parsers.ParameterParser
import dev.h4kt.ktorDocs.types.parameters.parsers.UUIDParameterParser
import dev.h4kt.ktorDocs.types.parameters.parsers.datetime.DurationParameterParser
import dev.h4kt.ktorDocs.types.parameters.parsers.datetime.InstantParameterParser
import dev.h4kt.ktorDocs.types.parameters.parsers.datetime.LocalDateParameterParser
import dev.h4kt.ktorDocs.types.parameters.parsers.datetime.LocalDateTimeParameterParser
import dev.h4kt.ktorDocs.types.parameters.parsers.primitives.IntParameterParser
import dev.h4kt.ktorDocs.types.parameters.parsers.primitives.LongParameterParser
import dev.h4kt.ktorDocs.types.parameters.parsers.primitives.StringParameterParser
import io.ktor.http.*
import io.ktor.util.reflect.*
import kotlin.reflect.KClass

class RouteParametersContainer : Iterable<RouteParameter<*>> {

    private val parameters = mutableListOf<RouteParameter<*>>()

    @KtorDocsDsl
    fun string(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, StringParameterParser)

    @KtorDocsDsl
    fun optionalString(
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalParameter(configure, StringParameterParser)

    @KtorDocsDsl
    fun int(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, IntParameterParser)

    @KtorDocsDsl
    fun optionalInt(
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalParameter(configure, IntParameterParser)

    @KtorDocsDsl
    fun long(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, LongParameterParser)

    @KtorDocsDsl
    fun optionalLong(
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalParameter(configure, LongParameterParser)

    @KtorDocsDsl
    fun uuid(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, UUIDParameterParser)

    @KtorDocsDsl
    fun optionalUUID(
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalParameter(configure, UUIDParameterParser)

    @KtorDocsDsl
    fun duration(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, DurationParameterParser)

    @KtorDocsDsl
    fun optionalDuration(
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalParameter(configure, DurationParameterParser)

    @KtorDocsDsl
    fun date(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, LocalDateParameterParser)

    @KtorDocsDsl
    fun optionalDate(
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalParameter(configure, LocalDateParameterParser)

    @KtorDocsDsl
    fun datetime(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, LocalDateTimeParameterParser)

    @KtorDocsDsl
    fun optionalDateTime(
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalParameter(configure, LocalDateTimeParameterParser)

    @KtorDocsDsl
    fun instant(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, InstantParameterParser)

    @KtorDocsDsl
    fun optionalInstant(
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalParameter(configure, InstantParameterParser)

    inline fun <reified T : Enum<T>> enum(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, EnumParameterParser(T::class))

    inline fun <reified T : Enum<T>> optionalEnum(
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalParameter(configure, EnumParameterParser(T::class))

    inline fun <reified T : Any> parameter(
        configure: RouteParameterBuilder.() -> Unit,
        parser: ParameterParser<T>
    ): RequiredRouteParameter<T> {
        val settings = RouteParameterBuilder().apply(configure)
        return RequiredRouteParameter(
            name = settings.name,
            typeInfo = typeInfo<T>(),
            description = settings.description,
            parser = parser
        ).also(::add)
    }

    inline fun <reified T : Any> optionalParameter(
        configure: RouteParameterBuilder.() -> Unit,
        parser: ParameterParser<T>
    ): OptionalRouteParameter<T> {
        val settings = RouteParameterBuilder().apply(configure)
        return OptionalRouteParameter(
            name = settings.name,
            typeInfo = typeInfo<T>(),
            description = settings.description,
            parser = parser
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
