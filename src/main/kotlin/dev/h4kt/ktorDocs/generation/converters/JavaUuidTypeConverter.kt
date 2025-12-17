package dev.h4kt.ktorDocs.generation.converters

import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType

class JavaUuidTypeConverter : TypeConverter() {

    override val priority = 900

    override fun doesSupport(type: KType): Boolean {
        return type.classifierKClass == UUID::class
    }

    override fun convert(
        type: KType,
        parentTypes: Collection<KType>,
        classifier: KClass<*>,
        convertDownstream: (type: KType) -> OpenApiSchema
    ): OpenApiSchema {
        return OpenApiSchema.String(
            format = "UUID",
            nullable = type.isMarkedNullable
        )
    }

}
