package dev.h4kt.ktorDocs.generation.converters.type

import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import kotlin.reflect.KClass
import kotlin.reflect.KType

abstract class TypeConverter(
    val priority: Int
) {

    abstract fun canConvert(
        type: KType
    ): Boolean

    abstract fun convert(
        type: KType,
        parentTypes: Collection<KType>,
        classifier: KClass<*>,
        convertDownstream: (type: KType) -> OpenApiSchema
    ): OpenApiSchema

    protected val KType.classifierKClass: KClass<*>
        get() = classifier as KClass<*>

}
