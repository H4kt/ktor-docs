package dev.h4kt.ktorDocs.generation

import dev.h4kt.ktorDocs.annotations.DocsName
import dev.h4kt.ktorDocs.exceptions.ClashingNamesException
import dev.h4kt.ktorDocs.generation.converters.TypeConverter
import dev.h4kt.ktorDocs.generation.results.SchemaGenerationResult
import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation

internal class SchemaGenerator(
    private val converters: List<TypeConverter>
) {

    private val typeMap = linkedMapOf<TypeMapKey, Triple<String, OpenApiSchema.Reference, OpenApiSchema>>()

    fun generateSchema(
        type: KType,
        parentTypes: List<KType> = emptyList()
    ): SchemaGenerationResult {
        val classifier = type.classifier as? KClass<*>
            ?: return SchemaGenerationResult.UnsupportedType(type)

        val key = TypeMapKey(
            kClass = classifier,
            arguments = type.arguments
        )

        val existingType = typeMap[key]
        if (existingType != null) {
            return SchemaGenerationResult.Success(existingType.second)
        }

        val converter = converters
            .filter { it.doesSupport(type) }
            .maxByOrNull { it.priority }
            ?: return SchemaGenerationResult.UnsupportedType(type)

        val schema = try {
            converter.convert(
                type = type,
                parentTypes = parentTypes,
                classifier = classifier,
                convertDownstream = convertDownstream@{ downstreamType ->
                    val result = generateSchema(downstreamType, listOf(type) + parentTypes)
                    if (result !is SchemaGenerationResult.Success) {
                        error("Failed to generate schema for $downstreamType: $result")
                    }

                    return@convertDownstream result.schema
                }
            )
        } catch (ex: Exception) {
            return SchemaGenerationResult.GenerationFailed(ex)
        }

        val hasArguments = type.arguments.isNotEmpty()

        if (hasArguments || schema.shouldBeInlined()) {
            return SchemaGenerationResult.Success(schema)
        }

        val name = classifier.findAnnotation<DocsName>()
            ?.value
            ?: classifier.java.name
                .substringAfterLast('.')
                .replace("$", "")

        val reference = OpenApiSchema.Reference("#/components/schemas/$name")

        typeMap[key] = Triple(name, reference, schema)

        return SchemaGenerationResult.Success(reference)
    }

    fun generate(): Map<String, OpenApiSchema> {
        val clashingNames = typeMap.entries
            .groupBy { it.value.first }
            .filter { it.value.size > 1 }
            .map { (name, entries) ->
                name to entries.map { (key, _) ->
                    key.kClass
                }
            }
            .toMap()

        if (clashingNames.isNotEmpty()) {
            throw ClashingNamesException(clashingNames)
        }

        return typeMap.values
            .associateBy { it.first }
            .mapValues { (_, value) -> value.third }
    }

    private fun OpenApiSchema.shouldBeInlined(): Boolean {
        return when (this) {
            is OpenApiSchema.Object -> false
            is OpenApiSchema.OneOf -> false
            is OpenApiSchema.Reference -> true
            is OpenApiSchema.Array -> true
            is OpenApiSchema.Boolean -> true
            is OpenApiSchema.Integer -> true
            is OpenApiSchema.Number -> true
            is OpenApiSchema.String -> enum == null
        }
    }

}
