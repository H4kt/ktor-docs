package dev.h4kt.ktorDocs.types.parameters

import io.ktor.http.*
import kotlin.reflect.KProperty

abstract class RouteParameter<TOutput : Any> {

    abstract val name: String
    abstract val description: String
    abstract val optional: Boolean

    open lateinit var value: TOutput

    abstract fun parse(parameters: Parameters)

    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ) = value

}