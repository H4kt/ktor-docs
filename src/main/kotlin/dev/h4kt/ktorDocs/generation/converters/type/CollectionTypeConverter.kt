package dev.h4kt.ktorDocs.generation.converters.type

import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.isSubclassOf

class CollectionTypeConverter : TypeConverter(priority = 1000) {

    override fun canConvert(type: KType): Boolean {
        return type.classifierKClass.isSubclassOf(Collection::class)
    }

    override fun convert(
        type: KType,
        parentTypes: Collection<KType>,
        classifier: KClass<*>,
        convertDownstream: (type: KType) -> OpenApiSchema
    ): OpenApiSchema {
        val itemType = resolveItemType(type, parentTypes)

        return OpenApiSchema.Array(
            items = convertDownstream(itemType),
            uniqueItems = classifier.isSubclassOf(Set::class),
            nullable = type.isMarkedNullable
        )
    }

    private fun resolveItemType(
        type: KType,
        parentTypes: Collection<KType>,
    ): KType {
        val baseType = type.arguments.first().type!!
        if (baseType.classifier is KClass<*>) {
            return baseType
        }

        if (baseType.classifier !is KTypeParameter) {
            error("Unknown classifier: $baseType")
        }

        var resolvedType = baseType

        for (parentType in parentTypes) {
            val classifier = resolvedType.classifier
            if (classifier is KClass<*>) {
                return resolvedType
            }

            if (classifier !is KTypeParameter) {
                error("Unknown classifier: $classifier")
            }

            val parentKClass = (parentType.classifier as? KClass<*>)
                ?: error("Failed to resolve collection item type")

            val typeParameter = parentKClass
                .typeParameters
                .first {
                    it.name == classifier.name
                }

            val index = parentKClass
                .typeParameters
                .indexOf(typeParameter)

            val typeProjection = parentType.arguments[index]

            resolvedType = typeProjection.type!!
        }

        val classifier = resolvedType.classifier
        if (classifier is KClass<*>) {
            return resolvedType
        }

        error("Failed to resolve collection item type")
    }

}
