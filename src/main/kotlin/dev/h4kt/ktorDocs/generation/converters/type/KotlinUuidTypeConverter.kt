package dev.h4kt.ktorDocs.generation.converters.type

import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class KotlinUuidTypeConverter : TypeConverter(priority = 900) {

    override fun canConvert(type: KType): Boolean {
        return type.classifierKClass == Uuid::class
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
