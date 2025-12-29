package dev.h4kt.ktorDocs.generation.converters.type

import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation

class DataObjectTypeConverter : TypeConverter(priority = 100) {

    override fun canConvert(type: KType): Boolean {
        return type.classifierKClass.isData && type.classifierKClass.objectInstance != null
    }

    override fun convert(
        type: KType,
        parentTypes: Collection<KType>,
        classifier: KClass<*>,
        convertDownstream: (type: KType) -> OpenApiSchema
    ): OpenApiSchema {
        val isNullable = type.isMarkedNullable
        val isDeprecated = type.findAnnotation<Deprecated>() != null || classifier.findAnnotation<Deprecated>() != null

        val sealedTypeDiscriminator = getSealedTypeDiscriminator(parentTypes)

        return if (sealedTypeDiscriminator != null) {
            val discriminatorValue = classifier.findAnnotation<SerialName>()
                ?.value
                ?: classifier.qualifiedName

            val discriminatorValueSchema = OpenApiSchema.String(const = discriminatorValue)

            OpenApiSchema.Object(
                properties = mapOf(sealedTypeDiscriminator to discriminatorValueSchema),
                required = listOf(sealedTypeDiscriminator),
                nullable = isNullable,
                deprecated = isDeprecated
            )
        } else {
            OpenApiSchema.Object(
                properties = emptyMap(),
                required = emptyList(),
                nullable = isNullable,
                deprecated = isDeprecated
            )
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun getSealedTypeDiscriminator(
        parentTypes: Collection<KType>,
    ): String? {
        val closestParentKClass = parentTypes.firstOrNull()
            ?.classifier as? KClass<*>
            ?: return null

        if (!closestParentKClass.isSealed) {
            return null
        }

        var lastDiscriminator = "type"

        parentTypes.forEach { parentType ->
            val parentKClass = parentType.classifier as? KClass<*>
                ?: return null

            if (!parentKClass.isSealed) {
                return lastDiscriminator
            }

            val annotation = parentKClass.findAnnotation<JsonClassDiscriminator>()
            if (annotation != null) {
                lastDiscriminator = annotation.discriminator
            }
        }

        return lastDiscriminator
    }

}
