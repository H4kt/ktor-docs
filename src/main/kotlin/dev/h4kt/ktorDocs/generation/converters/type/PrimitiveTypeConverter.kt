package dev.h4kt.ktorDocs.generation.converters.type

import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import kotlin.reflect.KClass
import kotlin.reflect.KType

class PrimitiveTypeConverter : TypeConverter(priority = 900) {

    private val primitiveTypes = listOf(
        String::class,
        Char::class,
        Byte::class,
        UByte::class,
        Short::class,
        UShort::class,
        Int::class,
        UInt::class,
        Long::class,
        ULong::class,
        Float::class,
        Double::class,
        Boolean::class
    )

    override fun canConvert(type: KType): Boolean {
        return type.classifierKClass in primitiveTypes
    }

    override fun convert(
        type: KType,
        parentTypes: Collection<KType>,
        classifier: KClass<*>,
        convertDownstream: (type: KType) -> OpenApiSchema
    ): OpenApiSchema {
        val isNullable = type.isMarkedNullable

        return when (classifier) {
            String::class,
            Char::class -> OpenApiSchema.String(
                nullable = isNullable
            )
            Byte::class,
            UByte::class,
            Short::class,
            UShort::class,
            Int::class,
            UInt::class,
            Long::class,
            ULong::class -> {
                val format = when (classifier) {
                    Byte::class -> OpenApiSchema.Integer.Format.INT8
                    UByte::class -> OpenApiSchema.Integer.Format.UINT8
                    Short::class -> OpenApiSchema.Integer.Format.INT16
                    UShort::class -> OpenApiSchema.Integer.Format.UINT16
                    Int::class -> OpenApiSchema.Integer.Format.INT32
                    UInt::class -> OpenApiSchema.Integer.Format.UINT32
                    Long::class -> OpenApiSchema.Integer.Format.INT64
                    ULong::class -> OpenApiSchema.Integer.Format.UINT64
                    else -> error("Unreachable code")
                }

                OpenApiSchema.Integer(
                    format = format,
                    nullable = isNullable
                )
            }
            Double::class,
            Float::class -> {
                val format = when (classifier) {
                    Double::class -> OpenApiSchema.Number.Format.DOUBLE
                    Float::class -> OpenApiSchema.Number.Format.FLOAT
                    else -> error("Unreachable code")
                }

                OpenApiSchema.Number(
                    format = format,
                    nullable = isNullable
                )
            }
            Boolean::class -> OpenApiSchema.Boolean(
                nullable = isNullable
            )
            else -> error("Unsupported type ${classifier.qualifiedName ?: classifier.simpleName ?: "Anonymous"}")
        }
    }

}
