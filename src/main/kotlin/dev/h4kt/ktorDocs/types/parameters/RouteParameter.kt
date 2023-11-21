package dev.h4kt.ktorDocs.types.parameters

import io.ktor.http.*
import io.ktor.util.reflect.*
import kotlin.reflect.KProperty

abstract class RouteParameter<TOutput : Any> {

    abstract val name: String
    abstract val typeInfo: TypeInfo
    abstract val description: String
    abstract val required: Boolean

    open lateinit var value: TOutput

    abstract fun parse(parameters: Parameters)

    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ) = value

}
