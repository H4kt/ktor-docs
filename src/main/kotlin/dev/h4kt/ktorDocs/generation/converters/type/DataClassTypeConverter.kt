package dev.h4kt.ktorDocs.generation.converters.type

import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

class DataClassTypeConverter : TypeConverter(priority = 100) {

    override fun canConvert(type: KType): Boolean {
        return type.classifierKClass.isData
    }

    override fun convert(
        type: KType,
        parentTypes: Collection<KType>,
        classifier: KClass<*>,
        convertDownstream: (type: KType) -> OpenApiSchema
    ): OpenApiSchema {

        val properties = mutableMapOf<String, OpenApiSchema>()

        val required = classifier.primaryConstructor
            ?.parameters
            ?.filterNot { it.isOptional }
            ?.mapNotNull { it.name }
            ?.toMutableList()
            ?: mutableListOf()

        val sealedTypeDiscriminator = getSealedTypeDiscriminator(parentTypes)
        if (sealedTypeDiscriminator != null) {
            val type = classifier.findAnnotation<SerialName>()
                ?.value
                ?: classifier.qualifiedName

            properties[sealedTypeDiscriminator] = OpenApiSchema.String(
                const = type
            )

            required.add(0, sealedTypeDiscriminator)
        }

        // TODO: might wanna use primary constructor parameters instead
        classifier.memberProperties.forEach { property ->
            val isDeprecated = property.findAnnotation<Deprecated>() != null

            val name = property.findAnnotation<SerialName>()
                ?.value
                ?: property.name

            properties[name] = convertDownstream(property.returnType)
                .asDeprecated(isDeprecated)
        }

        val isDeprecated = type.findAnnotation<Deprecated>() != null || classifier.findAnnotation<Deprecated>() != null

        return OpenApiSchema.Object(
            properties = properties,
            required = required,
            nullable = type.isMarkedNullable,
            deprecated = isDeprecated
        )
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
