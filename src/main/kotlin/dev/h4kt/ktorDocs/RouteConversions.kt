package dev.h4kt.ktorDocs

import dev.h4kt.ktorDocs.generation.SchemaGenerator
import dev.h4kt.ktorDocs.generation.results.SchemaGenerationResult
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRoute
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRouteBody
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRouteParameter
import dev.h4kt.ktorDocs.types.route.DocumentedRoute
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import org.slf4j.Logger

internal fun DocumentedRoute.toOpenApiRoute(
    logger: Logger,
    schemaGenerator: SchemaGenerator,
    method: HttpMethod,
    path: String,
    authentications: Set<String>
): OpenApiRoute {

    val logTarget = "$method $path"

    val parameters = mutableListOf<OpenApiRouteParameter>()

    this.parameters.path.forEach { parameter ->
        val kotlinType = parameter.typeInfo.kotlinType
        if (kotlinType == null) {
            logger.warn("Failed to generate path parameter ${parameter.name} schema for $logTarget: no kotlin type found")
            return@forEach
        }

        val schemaResult = schemaGenerator.generateSchema(parameter.typeInfo.kotlinType!!)
        if (schemaResult !is SchemaGenerationResult.Success) {
            logger.warn("Failed to generate path parameter ${parameter.name} schema for $logTarget: $schemaResult")
            return@forEach
        }

        parameters += OpenApiRouteParameter(
            type = OpenApiRouteParameter.Type.PATH,
            name = parameter.name,
            schema = schemaResult.schema,
            required = parameter.required
        )
    }

    this.parameters.query.forEach { parameter ->
        val kotlinType = parameter.typeInfo.kotlinType
        if (kotlinType == null) {
            logger.warn("Failed to generate query parameter ${parameter.name} schema for $logTarget: no kotlin type found")
            return@forEach
        }

        val schemaResult = schemaGenerator.generateSchema(parameter.typeInfo.kotlinType!!)
        if (schemaResult !is SchemaGenerationResult.Success) {
            logger.warn("Failed to generate query parameter ${parameter.name} schema for $logTarget: $schemaResult")
            return@forEach
        }

        parameters += OpenApiRouteParameter(
            type = OpenApiRouteParameter.Type.QUERY,
            name = parameter.name,
            schema = schemaResult.schema,
            required = parameter.required
        )
    }

    // TODO: gather content type info
    val contentType = ContentType.Application.Json

    val requestBody = requestBody?.let {
        val kotlinType = it.kotlinType
            if (kotlinType == null) {
                logger.warn("Failed to generate request body schema for $logTarget: no kotlin type found")
                return@let null
        }

        val schemaResult = schemaGenerator.generateSchema(kotlinType)
        if (schemaResult !is SchemaGenerationResult.Success) {
            logger.warn("Failed to generate request body schema for $logTarget: $schemaResult")
            return@let null
        }

        return@let OpenApiRouteBody(
            content = mapOf(
                contentType to OpenApiRouteBody.Schema(
                    schema = schemaResult.schema
                )
            )
        )
    }

    val mappedResponses = mutableMapOf<String, OpenApiRouteBody>()

    responses.forEach { (statusCode, response) ->
        val bodyKotlinType = response.body.kotlinType
        if (bodyKotlinType == null) {
            logger.warn("Failed to generate response body schema for $logTarget -> $statusCode: no kotlin type found")
            return@forEach
        }

        val content = if (response.body.type == Unit::class) {
            emptyMap()
        } else {
            val schemaResult = schemaGenerator.generateSchema(bodyKotlinType)
            if (schemaResult !is SchemaGenerationResult.Success) {
                logger.warn("Failed to generate response body schema for $logTarget -> $statusCode: $schemaResult")
                return@forEach
            }

            mapOf(
                contentType to OpenApiRouteBody.Schema(
                    schema = schemaResult.schema
                )
            )
        }

        mappedResponses[statusCode.value.toString()] = OpenApiRouteBody(
            content = content,
            description = response.description ?: ""
        )
    }

    return OpenApiRoute(
        tags = tags,
        summary = description,
        security = authentications.map { mapOf(it to emptyList()) },
        parameters = parameters,
        requestBody = requestBody,
        responses = mappedResponses
    )
}
