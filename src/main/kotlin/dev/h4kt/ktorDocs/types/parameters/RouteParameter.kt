package dev.h4kt.ktorDocs.types.parameters

import io.ktor.http.*
import io.ktor.util.reflect.*
import kotlin.reflect.KProperty

sealed class RouteParameter<TOutput> {

    abstract val name: String
    abstract val typeInfo: TypeInfo
    abstract val description: String

    abstract val required: Boolean

    protected abstract val value: TOutput

    abstract fun parse(parameters: Parameters)

    abstract operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): TOutput

}
