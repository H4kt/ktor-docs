package dev.h4kt.ktorDocs.generation.converters.type

import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KType

class JavaUuidTypeConverter : TypeConverter(priority = 900) {

    override fun canConvert(type: KType): Boolean {
        return type.classifierKClass == UUID::class
    }

    override fun convert(
        type: KType,
        parentTypes: Collection<KType>,
        classifier: KClass<*>,
        convertDownstream: (type: KType) -> OpenApiSchema
    ): OpenApiSchema {
        return OpenApiSchema.String(
            format = "uuid",
            nullable = type.isMarkedNullable
        )
    }

}
