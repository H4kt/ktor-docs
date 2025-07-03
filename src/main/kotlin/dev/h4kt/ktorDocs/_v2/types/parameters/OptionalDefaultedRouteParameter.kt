package dev.h4kt.ktorDocs._v2.types.parameters

import dev.h4kt.ktorDocs.types.parameters.parsers.ParameterParser
import io.ktor.util.reflect.TypeInfo
import kotlin.reflect.KProperty

class OptionalDefaultedRouteParameter<T : Any>(
    name: String,
    typeInfo: TypeInfo,
    description: String,
    parser: ParameterParser<T>,
    private val defaultValue: T
) : OptionalRouteParameter<T>(
    name = name,
    typeInfo = typeInfo,
    description = description,
    parser = parser
) {

    override operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): T = value ?: defaultValue

}
