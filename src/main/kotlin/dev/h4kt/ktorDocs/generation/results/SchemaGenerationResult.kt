package dev.h4kt.ktorDocs.generation.results

import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import kotlin.reflect.KType

sealed interface SchemaGenerationResult {
    sealed interface Error : SchemaGenerationResult

    data class UnsupportedType(
        val type: KType
    ) : Error

    data class GenerationFailed(
        val cause: Throwable
    ) : Error

    data class Success(
        val schema: OpenApiSchema
    ) : SchemaGenerationResult
}
