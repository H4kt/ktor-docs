package dev.h4kt.ktorDocs.generation.converters.type

import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import kotlinx.serialization.SerialName
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
        @Suppress("UNCHECKED_CAST")
        val enumClass = classifier.java as Class<Enum<*>>

        val values = enumClass.enumConstants
            .map { enum ->
                val field = enum.declaringJavaClass.getField(enum.name)
                val serialNameAnnotation = field.getAnnotation(SerialName::class.java)

                return@map serialNameAnnotation?.value ?: field.name
            }

        return OpenApiSchema.String(
            enum = values
        )
    }

}
