package dev.h4kt.ktorDocs.generation

import dev.h4kt.ktorDocs.generation.converters.type.CollectionTypeConverter
import dev.h4kt.ktorDocs.generation.converters.type.DataClassTypeConverter
import dev.h4kt.ktorDocs.generation.converters.type.DataObjectTypeConverter
import dev.h4kt.ktorDocs.generation.converters.type.EnumTypeConverter
import dev.h4kt.ktorDocs.generation.converters.type.JavaUuidTypeConverter
import dev.h4kt.ktorDocs.generation.converters.type.KotlinTimeTypeConverter
import dev.h4kt.ktorDocs.generation.converters.type.KotlinUuidTypeConverter
import dev.h4kt.ktorDocs.generation.converters.type.KotlinXDateTimeTypeConverter
import dev.h4kt.ktorDocs.generation.converters.type.PrimitiveTypeConverter
import dev.h4kt.ktorDocs.generation.converters.type.SealedTypeConverter
import dev.h4kt.ktorDocs.generation.inputs.TestSealedClass
import dev.h4kt.ktorDocs.generation.inputs.TestSealedClassWithDiscriminator
import dev.h4kt.ktorDocs.generation.results.SchemaGenerationResult
import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import java.util.*
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.datetime.Instant as KotlinXDateTimeInstant
import kotlin.time.Instant as KotlinTimeInstant

class SchemaGeneratorTest {

    private val generator = SchemaGenerator(
        listOf(
            CollectionTypeConverter(),
            DataClassTypeConverter(),
            DataObjectTypeConverter(),
            EnumTypeConverter(),
            JavaUuidTypeConverter(),
            KotlinTimeTypeConverter(),
            KotlinUuidTypeConverter(),
            KotlinXDateTimeTypeConverter(),
            PrimitiveTypeConverter(),
            DataClassTypeConverter(),
            SealedTypeConverter()
        )
    )

    @Test
    fun `test java UUID generation`() {
        generator.testGenerateSchema(
            type = typeOf<UUID>(),
            schema = OpenApiSchema.String(format = "uuid")
        )
    }

    @Test
    fun `test nullable java UUID generation`() {
        generator.testGenerateSchema(
            type = typeOf<UUID?>(),
            schema = OpenApiSchema.String(
                format = "uuid",
                nullable = true
            )
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `test kotlin Uuid generation`() {
        generator.testGenerateSchema(
            type = typeOf<Uuid>(),
            schema = OpenApiSchema.String(format = "uuid")
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `test nullable kotlin Uuid generation`() {
        generator.testGenerateSchema(
            type = typeOf<Uuid?>(),
            schema = OpenApiSchema.String(
                format = "uuid",
                nullable = true
            )
        )
    }

    // --- kotlin.time start --- //
    @Test
    fun `test kotlin time Instant generation`() {
        generator.testGenerateSchema(
            type = typeOf<KotlinTimeInstant>(),
            schema = OpenApiSchema.String(format = "date-time")
        )
    }

    @Test
    fun `test nullable kotlin time Instant generation`() {
        generator.testGenerateSchema(
            type = typeOf<KotlinTimeInstant?>(),
            schema = OpenApiSchema.String(
                format = "date-time",
                nullable = true
            )
        )
    }

    @Test
    fun `test kotlin time Duration generation`() {
        generator.testGenerateSchema(
            type = typeOf<Duration>(),
            schema = OpenApiSchema.String(format = "duration")
        )
    }

    @Test
    fun `test nullable kotlin time Duration generation`() {
        generator.testGenerateSchema(
            type = typeOf<Duration?>(),
            schema = OpenApiSchema.String(
                format = "duration",
                nullable = true
            )
        )
    }
    // --- kotlin.time end --- //

    // --- kotlinx.datetime start --- //
    @Test
    fun `test kotlinx datetime Instant generation`() {
        generator.testGenerateSchema(
            type = typeOf<KotlinXDateTimeInstant>(),
            schema = OpenApiSchema.String(format = "date-time")
        )
    }

    @Test
    fun `test nullable kotlinx datetime Instant generation`() {
        generator.testGenerateSchema(
            type = typeOf<KotlinXDateTimeInstant?>(),
            schema = OpenApiSchema.String(
                format = "date-time",
                nullable = true
            )
        )
    }

    @Test
    fun `test kotlinx datetime LocalDate generation`() {
        generator.testGenerateSchema(
            type = typeOf<LocalDate>(),
            schema = OpenApiSchema.String(format = "date")
        )
    }

    @Test
    fun `test nullable kotlinx datetime LocalDate generation`() {
        generator.testGenerateSchema(
            type = typeOf<LocalDate?>(),
            schema = OpenApiSchema.String(
                format = "date",
                nullable = true
            )
        )
    }

    @Test
    fun `test kotlinx datetime LocalDateTime generation`() {
        generator.testGenerateSchema(
            type = typeOf<LocalDateTime>(),
            schema = OpenApiSchema.String(format = "date-time-local")
        )
    }

    @Test
    fun `test nullable kotlinx datetime LocalDateTime generation`() {
        generator.testGenerateSchema(
            type = typeOf<LocalDateTime?>(),
            schema = OpenApiSchema.String(
                format = "date-time-local",
                nullable = true
            )
        )
    }
    // --- kotlinx.datetime end --- //

    // --- Primitive types start --- //
    @Test
    fun `test String generation`() {
        generator.testGenerateSchema(
            type = typeOf<String>(),
            schema = OpenApiSchema.String()
        )
    }

    @Test
    fun `test nullable String generation`() {
        generator.testGenerateSchema(
            type = typeOf<String?>(),
            schema = OpenApiSchema.String(nullable = true)
        )
    }

    @Test
    fun `test Char generation`() {
        generator.testGenerateSchema(
            type = typeOf<Char>(),
            schema = OpenApiSchema.String(format = "char")
        )
    }

    @Test
    fun `test nullable Char generation`() {
        generator.testGenerateSchema(
            type = typeOf<Char?>(),
            schema = OpenApiSchema.String(
                format = "char",
                nullable = true
            )
        )
    }

    @Test
    fun `test Byte generation`() {
        generator.testGenerateSchema(
            type = typeOf<Byte>(),
            schema = OpenApiSchema.Integer(
                format = INT8
            )
        )
    }

    @Test
    fun `test nullable Byte generation`() {
        generator.testGenerateSchema(
            type = typeOf<Byte?>(),
            schema = OpenApiSchema.Integer(
                format = INT8,
                nullable = true
            )
        )
    }

    @Test
    fun `test UByte generation`() {
        generator.testGenerateSchema(
            type = typeOf<UByte>(),
            schema = OpenApiSchema.Integer(
                format = UINT8
            )
        )
    }

    @Test
    fun `test nullable UByte generation`() {
        generator.testGenerateSchema(
            type = typeOf<UByte?>(),
            schema = OpenApiSchema.Integer(
                format = UINT8,
                nullable = true
            )
        )
    }

    @Test
    fun `test Short generation`() {
        generator.testGenerateSchema(
            type = typeOf<Short>(),
            schema = OpenApiSchema.Integer(
                format = INT16
            )
        )
    }

    @Test
    fun `test nullable Short generation`() {
        generator.testGenerateSchema(
            type = typeOf<Short?>(),
            schema = OpenApiSchema.Integer(
                format = INT16,
                nullable = true
            )
        )
    }

    @Test
    fun `test UShort generation`() {
        generator.testGenerateSchema(
            type = typeOf<UShort>(),
            schema = OpenApiSchema.Integer(
                format = UINT16
            )
        )
    }

    @Test
    fun `test nullable UShort generation`() {
        generator.testGenerateSchema(
            type = typeOf<UShort?>(),
            schema = OpenApiSchema.Integer(
                format = UINT16,
                nullable = true
            )
        )
    }

    @Test
    fun `test Int generation`() {
        generator.testGenerateSchema(
            type = typeOf<Int>(),
            schema = OpenApiSchema.Integer(
                format = INT32
            )
        )
    }

    @Test
    fun `test nullable Int generation`() {
        generator.testGenerateSchema(
            type = typeOf<Int?>(),
            schema = OpenApiSchema.Integer(
                format = INT32,
                nullable = true
            )
        )
    }

    @Test
    fun `test UInt generation`() {
        generator.testGenerateSchema(
            type = typeOf<UInt>(),
            schema = OpenApiSchema.Integer(
                format = UINT32
            )
        )
    }

    @Test
    fun `test nullable UInt generation`() {
        generator.testGenerateSchema(
            type = typeOf<UInt?>(),
            schema = OpenApiSchema.Integer(
                format = UINT32,
                nullable = true
            )
        )
    }

    @Test
    fun `test Long generation`() {
        generator.testGenerateSchema(
            type = typeOf<Long>(),
            schema = OpenApiSchema.Integer(
                format = INT64
            )
        )
    }

    @Test
    fun `test nullable Long generation`() {
        generator.testGenerateSchema(
            type = typeOf<Long?>(),
            schema = OpenApiSchema.Integer(
                format = INT64,
                nullable = true
            )
        )
    }

    @Test
    fun `test ULong generation`() {
        generator.testGenerateSchema(
            type = typeOf<ULong>(),
            schema = OpenApiSchema.Integer(
                format = UINT64
            )
        )
    }

    @Test
    fun `test nullable ULong generation`() {
        generator.testGenerateSchema(
            type = typeOf<ULong?>(),
            schema = OpenApiSchema.Integer(
                format = UINT64,
                nullable = true
            )
        )
    }

    @Test
    fun `test Float generation`() {
        generator.testGenerateSchema(
            type = typeOf<Float>(),
            schema = OpenApiSchema.Number(
                format = FLOAT
            )
        )
    }

    @Test
    fun `test nullable Float generation`() {
        generator.testGenerateSchema(
            type = typeOf<Float?>(),
            schema = OpenApiSchema.Number(
                format = FLOAT,
                nullable = true
            )
        )
    }

    @Test
    fun `test Double generation`() {
        generator.testGenerateSchema(
            type = typeOf<Double>(),
            schema = OpenApiSchema.Number(
                format = DOUBLE
            )
        )
    }

    @Test
    fun `test nullable Double generation`() {
        generator.testGenerateSchema(
            type = typeOf<Double?>(),
            schema = OpenApiSchema.Number(
                format = DOUBLE,
                nullable = true
            )
        )
    }

    @Test
    fun `test Boolean generation`() {
        generator.testGenerateSchema(
            type = typeOf<Boolean>(),
            schema = OpenApiSchema.Boolean()
        )
    }

    @Test
    fun `test nullable Boolean generation`() {
        generator.testGenerateSchema(
            type = typeOf<Boolean?>(),
            schema = OpenApiSchema.Boolean(nullable = true)
        )
    }
    // --- Primitive types end --- //

    // --- Sealed classes start --- //
    @Test
    fun `test sealed class generation`() {
        generator.testGenerateSchema(
            type = typeOf<TestSealedClass>(),
            schema = OpenApiSchema.OneOf(
                variants = listOf(
                    OpenApiSchema.Object(
                        properties = mapOf(
                            "type" to OpenApiSchema.String(const = "BAR")
                        ),
                        required = listOf("type")
                    ),
                    OpenApiSchema.Object(
                        properties = mapOf(
                            "type" to OpenApiSchema.String(const = "FOO")
                        ),
                        required = listOf("type")
                    )
                )
            )
        )
    }

    @Test
    fun `test sealed class with custom discriminator generation`() {
        generator.testGenerateSchema(
            type = typeOf<TestSealedClassWithDiscriminator>(),
            schema = OpenApiSchema.OneOf(
                variants = listOf(
                    OpenApiSchema.Object(
                        properties = mapOf(
                            "version" to OpenApiSchema.String(const = "1")
                        ),
                        required = listOf("version")
                    ),
                    OpenApiSchema.Object(
                        properties = mapOf(
                            "version" to OpenApiSchema.String(const = "2")
                        ),
                        required = listOf("version")
                    )
                )
            )
        )
    }

    private fun SchemaGenerator.testGenerateSchema(
        type: KType,
        schema: OpenApiSchema
    ) {
        val result = generateSchema(
            type = type,
            allowReferences = false
        )

        assertIs<SchemaGenerationResult.Success>(result)
        assertEquals(schema, result.schema)
    }

}
