package dev.h4kt.ktorDocs._v2.types.parameters

import io.ktor.http.*
import io.ktor.util.reflect.*
import kotlin.reflect.KProperty

sealed class RouteParameter<T> {

    abstract val name: String
    abstract val typeInfo: TypeInfo
    abstract val description: String

    abstract val required: Boolean

    protected abstract val value: T

    abstract fun parse(parameters: Parameters)

    abstract operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): T

}
