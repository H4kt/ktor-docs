package dev.h4kt.ktorDocs.generation.converters.type

import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlinx.datetime.Instant

class KotlinXDateTimeTypeConverter : TypeConverter(priority = 900) {

    private val supportedTypes = listOf(
        Instant::class,
        LocalDate::class,
        LocalDateTime::class
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
            LocalDate::class -> "date"
            LocalDateTime::class -> "date-time-local"
            else -> error("Unsupported classifier $classifier")
        }

        return OpenApiSchema.String(
            format = format,
            nullable = type.isMarkedNullable
        )
    }

}
