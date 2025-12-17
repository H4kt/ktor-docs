package dev.h4kt.ktorDocs.generation.converters.type

import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.time.Duration
import kotlin.time.Instant

class KotlinTimeTypeConverter : TypeConverter(priority = 900) {

    private val supportedTypes = listOf(
        Instant::class,
        Duration::class
    )

    override fun canConvert(type: KType): Boolean {
        return type.classifierKClass in supportedTypes
    }

    override fun convert(
        type: KType,
        parentTypes: Collection<KType>,
        classifier: KClass<*>,
        convertDownstream: (type: KType) -> OpenApiSchema
    ): OpenApiSchema {
        val format = when (classifier) {
            Instant::class -> "date-time"
            Duration::class -> "duration"
            else -> error("Unsupported classifier $classifier")
        }

        return OpenApiSchema.String(
            format = format,
            nullable = type.isMarkedNullable
        )
    }

}
