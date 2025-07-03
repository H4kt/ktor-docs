package dev.h4kt.ktorDocs._v2.types.parameters

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
import io.ktor.http.Parameters
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.typeInfo
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import java.util.UUID
import kotlin.time.Duration

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
    fun optionalString(
        defaultValue: String,
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalDefaultedParameter(configure, StringParameterParser, defaultValue)

    @KtorDocsDsl
    fun int(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, IntParameterParser)

    @KtorDocsDsl
    fun optionalInt(
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalParameter(configure, IntParameterParser)

    @KtorDocsDsl
    fun optionalInt(
        defaultValue: Int,
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalDefaultedParameter(configure, IntParameterParser, defaultValue)

    @KtorDocsDsl
    fun long(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, LongParameterParser)

    @KtorDocsDsl
    fun optionalLong(
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalParameter(configure, LongParameterParser)

    @KtorDocsDsl
    fun optionalLong(
        defaultValue: Long,
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalDefaultedParameter(configure, LongParameterParser, defaultValue)

    @KtorDocsDsl
    fun uuid(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, UUIDParameterParser)

    @KtorDocsDsl
    fun optionalUuid(
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalParameter(configure, UUIDParameterParser)

    @KtorDocsDsl
    fun optionalUuid(
        defaultValue: UUID,
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalDefaultedParameter(configure, UUIDParameterParser, defaultValue)

    @KtorDocsDsl
    fun duration(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, DurationParameterParser)

    @KtorDocsDsl
    fun optionalDuration(
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalParameter(configure, DurationParameterParser)

    @KtorDocsDsl
    fun optionalDuration(
        defaultValue: Duration,
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalDefaultedParameter(configure, DurationParameterParser, defaultValue)

    @KtorDocsDsl
    fun date(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, LocalDateParameterParser)

    @KtorDocsDsl
    fun optionalDate(
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalParameter(configure, LocalDateParameterParser)

    @KtorDocsDsl
    fun optionalDate(
        defaultValue: LocalDate,
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalDefaultedParameter(configure, LocalDateParameterParser, defaultValue)

    @KtorDocsDsl
    fun datetime(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, LocalDateTimeParameterParser)

    @KtorDocsDsl
    fun optionalDateTime(
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalParameter(configure, LocalDateTimeParameterParser)

    @KtorDocsDsl
    fun optionalDateTime(
        defaultValue: LocalDateTime,
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalDefaultedParameter(configure, LocalDateTimeParameterParser, defaultValue)

    @KtorDocsDsl
    fun instant(
        configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, InstantParameterParser)

    @KtorDocsDsl
    fun optionalInstant(
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalParameter(configure, InstantParameterParser)

    @KtorDocsDsl
    fun optionalInstant(
        defaultValue: Instant,
        configure: RouteParameterBuilder.() -> Unit
    ) = optionalDefaultedParameter(configure, InstantParameterParser, defaultValue)

    inline fun <reified T : Enum<T>> enum(
        noinline configure: RouteParameterBuilder.() -> Unit
    ) = parameter(configure, EnumParameterParser(T::class))

    inline fun <reified T : Enum<T>> optionalEnum(
        noinline configure: RouteParameterBuilder.() -> Unit
    ) = optionalParameter(configure, EnumParameterParser(T::class))

    inline fun <reified T : Enum<T>> optionalEnum(
        defaultValue: T,
        noinline configure: RouteParameterBuilder.() -> Unit
    ) = optionalDefaultedParameter(configure, EnumParameterParser(T::class), defaultValue)

    fun <T : Any> parameter(
        configure: RouteParameterBuilder.() -> Unit,
        parser: ParameterParser<T>,
        typeInfo: TypeInfo
    ): RequiredRouteParameter<T> {
        val settings = RouteParameterBuilder().apply(configure)
        return RequiredRouteParameter(
            name = settings.name,
            typeInfo = typeInfo,
            description = settings.description,
            parser = parser
        ).also(::add)
    }

    inline fun <reified T : Any> parameter(
        noinline configure: RouteParameterBuilder.() -> Unit,
        parser: ParameterParser<T>
    ) = parameter(
        configure = configure,
        parser = parser,
        typeInfo = typeInfo<T>()
    )

    fun <T : Any> optionalParameter(
        configure: RouteParameterBuilder.() -> Unit,
        parser: ParameterParser<T>,
        typeInfo: TypeInfo
    ): OptionalRouteParameter<T> {
        val settings = RouteParameterBuilder().apply(configure)
        return OptionalRouteParameter(
            name = settings.name,
            typeInfo = typeInfo,
            description = settings.description,
            parser = parser
        ).also(::add)
    }

    inline fun <reified T : Any> optionalParameter(
        noinline configure: RouteParameterBuilder.() -> Unit,
        parser: ParameterParser<T>
    ) = optionalParameter(
        configure = configure,
        parser = parser,
        typeInfo = typeInfo<T>()
    )

    fun <T : Any> optionalDefaultedParameter(
        configure: RouteParameterBuilder.() -> Unit,
        parser: ParameterParser<T>,
        defaultValue: T,
        typeInfo: TypeInfo
    ): OptionalDefaultedRouteParameter<T> {
        val settings = RouteParameterBuilder().apply(configure)
        return OptionalDefaultedRouteParameter(
            name = settings.name,
            typeInfo = typeInfo,
            description = settings.description,
            parser = parser,
            defaultValue = defaultValue
        ).also(::add)
    }

    inline fun <reified T : Any> optionalDefaultedParameter(
        noinline configure: RouteParameterBuilder.() -> Unit,
        parser: ParameterParser<T>,
        defaultValue: T
    ) = optionalDefaultedParameter(
        configure = configure,
        parser = parser,
        defaultValue = defaultValue,
        typeInfo = typeInfo<T>()
    )

    private fun add(parameter: RouteParameter<*>) {
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
