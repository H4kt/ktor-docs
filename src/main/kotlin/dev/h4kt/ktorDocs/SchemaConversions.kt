package dev.h4kt.ktorDocs

import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.*
import kotlin.time.Duration

private val builtInTypes = mapOf(
    String::class to OpenApiSchema.String(),
    Char::class to OpenApiSchema.String(),
    Int::class to OpenApiSchema.Integer(
        format = OpenApiSchema.Integer.Format.INT32
    ),
    Short::class to OpenApiSchema.Integer(
        minimum = Short.MIN_VALUE.toInt(),
        maximum = Short.MAX_VALUE.toInt(),
    ),
    Byte::class to OpenApiSchema.Integer(
        minimum = Byte.MIN_VALUE.toInt(),
        maximum = Byte.MAX_VALUE.toInt(),
    ),
    Long::class to OpenApiSchema.Integer(
        format = OpenApiSchema.Integer.Format.INT64
    ),
    Boolean::class to OpenApiSchema.Boolean(),
    UUID::class to OpenApiSchema.String(
        format = "UUID"
    ),
    Instant::class to OpenApiSchema.String(
        format = "date-time"
    ),
    LocalDateTime::class to OpenApiSchema.String(
        format = "date-time"
    ),
    LocalDate::class to OpenApiSchema.String(
        format = "date"
    ),
    Duration::class to OpenApiSchema.String(
        format = "duration"
    )
)

fun KType.toOpenApiSchema(): OpenApiSchema {
    return toOpenApiSchema(emptyList())
}

fun KType.toOpenApiSchema(
    parentTypes: List<KType>
): OpenApiSchema {

    val type = when (val classifier = classifier) {
        is KClass<*> -> classifier
        is KTypeParameter -> {

            // TODO: think of a better way of resolving downstream generics

            var resolvedType: KClass<*>? = null

            for (parentType in parentTypes.reversed()) {

                val parentClass = (parentType.classifier as? KClass<*>)
                    ?: continue

                val typeParameter = parentClass
                    .typeParameters
                    .first {
                        it.name == classifier.name
                    }

                val index = parentClass
                    .typeParameters
                    .indexOf(typeParameter)

                val typeProjection = parentType.arguments[index]

                val type = (typeProjection.type?.classifier as? KClass<*>)
                if (type != null) {
                    resolvedType = type
                    break
                }

            }

            resolvedType!!
        }
        else -> throw UnsupportedOperationException()
    }

    return when {
        type in builtInTypes -> builtInTypes[type]!!
        type.isSubclassOf(Enum::class) -> toOpenApiEnum()
        type.isSubclassOf(List::class) -> toOpenApiArray()
        type.isSealed -> toOpenApiOneOf(parentTypes)
        else -> toOpenApiObject(parentTypes)
    }

}

private fun KType.toOpenApiArray(): OpenApiSchema.Array {
    return OpenApiSchema.Array(
        items = arguments.first().type!!.toOpenApiSchema()
    )
}

@Suppress("UNCHECKED_CAST")
private fun KType.toOpenApiEnum(): OpenApiSchema.String {

    val type = classifier as? KClass<*>
        ?: throw UnsupportedOperationException()

    val values = (type.java as Class<Enum<*>>).enumConstants
        .map { it.name }

    return OpenApiSchema.String(
        enum = values
    )
}

private fun KType.toOpenApiOneOf(
    parentTypes: List<KType>
): OpenApiSchema.OneOf {

    val type = classifier as? KClass<*>
        ?: throw UnsupportedOperationException()

    val variants = type.sealedSubclasses.map {
        it.createType().toOpenApiObject(parentTypes + this)
    }

    return OpenApiSchema.OneOf(
        variants = variants
    )
}

private fun KType.toOpenApiObject(
    parentTypes: List<KType>
): OpenApiSchema.Object {

    val type = this.classifier as? KClass<*>
        ?: throw UnsupportedOperationException()

    val serialNameAnnotation = type.findAnnotation<SerialName>()

    val properties = type.memberProperties
        .associateBy(
            keySelector = { it.name },
            valueTransform = {
                it.returnType.toOpenApiSchema(parentTypes + this)
            }
        )

    val required = type.primaryConstructor
        ?.parameters
        ?.filter { !it.isOptional }
        ?.mapNotNull { it.name }
        ?: emptyList()

    return OpenApiSchema.Object(
        title = serialNameAnnotation?.value ?: "",
        properties = properties,
        required = required
    )
}
