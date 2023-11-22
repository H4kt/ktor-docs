package dev.h4kt.ktorDocs.types.parameters

import dev.h4kt.ktorDocs.types.parameters.parsers.ParameterParser
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.util.reflect.*
import kotlin.reflect.KProperty

class OptionalRouteParameter<T : Any>(
    override val name: String,
    override val typeInfo: TypeInfo,
    override val description: String,
    private val parser: ParameterParser<T>
) : RouteParameter<T?>() {

    override var value: T? = null

    override val required: Boolean
        get() = false

    override fun parse(parameters: Parameters) {

        try {
            value = parser.parse(parameters, name)
        } catch (ex: Exception) {
            throw ParameterConversionException(
                parameterName = name,
                type = typeInfo.type.simpleName ?: typeInfo.type.toString(),
                cause = ex
            )
        }

    }

    override operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): T? = value

}
