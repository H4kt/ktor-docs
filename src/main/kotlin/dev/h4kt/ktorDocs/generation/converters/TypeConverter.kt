package dev.h4kt.ktorDocs.generation.converters

import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import kotlin.reflect.KClass
import kotlin.reflect.KType

abstract class TypeConverter {

    abstract val priority: Int

    abstract fun doesSupport(
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
