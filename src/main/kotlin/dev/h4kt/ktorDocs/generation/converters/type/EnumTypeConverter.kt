package dev.h4kt.ktorDocs.generation.converters.type

import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf

class EnumTypeConverter : TypeConverter(priority = 1000) {

    override fun canConvert(type: KType): Boolean {
        return type.classifierKClass.isSubclassOf(Enum::class)
    }

    override fun convert(
        type: KType,
        parentTypes: Collection<KType>,
        classifier: KClass<*>,
        convertDownstream: (type: KType) -> OpenApiSchema
    ): OpenApiSchema {
        val values = (classifier.java as Class<Enum<*>>).enumConstants
            .map { it.name }

        return OpenApiSchema.String(
            enum = values
        )
    }

}
