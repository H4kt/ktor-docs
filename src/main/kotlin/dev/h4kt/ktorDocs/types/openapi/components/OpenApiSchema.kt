package dev.h4kt.ktorDocs.types.openapi.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable/*(with = OpenApiSchemaSerializer::class)*/
sealed interface OpenApiSchema {

    val title: kotlin.String
    val nullable: kotlin.Boolean
    val deprecated: kotlin.Boolean

    @Serializable
    @SerialName("string")
    data class String(
        val format: kotlin.String? = null,
        val pattern: kotlin.String? = null,
        val enum: List<kotlin.String>? = null,
        override val title: kotlin.String = "",
        override val nullable: kotlin.Boolean = false,
        override val deprecated: kotlin.Boolean = false
    ) : OpenApiSchema

    @Serializable
    @SerialName("number")
    data class Number(
        val minimum: Int? = null,
        val exclusiveMinimum: Boolean? = null,
        val maximum: Int? = null,
        val exclusiveMaximum: Boolean? = null,
        val format: Format? = null,
        override val title: kotlin.String = "",
        override val nullable: kotlin.Boolean = false,
        override val deprecated: kotlin.Boolean = false
    ) : OpenApiSchema {

        enum class Format {
            FLOAT, DOUBLE
        }

    }

    @Serializable
    @SerialName("integer")
    data class Integer(
        val minimum: Int? = null,
        val exclusiveMinimum: Boolean? = null,
        val maximum: Int? = null,
        val exclusiveMaximum: Boolean? = null,
        val format: Format? = null,
        override val title: kotlin.String = "",
        override val nullable: kotlin.Boolean = false,
        override val deprecated: kotlin.Boolean = false
    ) : OpenApiSchema {

        enum class Format {
            INT32, INT64
        }

    }

    @Serializable
    @SerialName("boolean")
    data class Boolean(
        override val title: kotlin.String = "",
        override val nullable: kotlin.Boolean = false,
        override val deprecated: kotlin.Boolean = false
    ) : OpenApiSchema

    @Serializable
    @SerialName("array")
    data class Array(
        val items: OpenApiSchema,
        override val title: kotlin.String = "",
        override val nullable: kotlin.Boolean = false,
        override val deprecated: kotlin.Boolean = false
    ) : OpenApiSchema

    @Serializable
    @SerialName("object")
    data class Object(
        val properties: Map<kotlin.String, OpenApiSchema> = emptyMap(),
        val required: List<kotlin.String> = emptyList(),
        override val title: kotlin.String = "",
        override val nullable: kotlin.Boolean = false,
        override val deprecated: kotlin.Boolean = false
    ) : OpenApiSchema

    @Serializable
    @SerialName("ref")
    data class Reference(
        @SerialName("\$ref") val ref: kotlin.String,
        override val title: kotlin.String = "",
        override val nullable: kotlin.Boolean = false,
        override val deprecated: kotlin.Boolean = false
    ) : OpenApiSchema

    @Serializable
    @SerialName("oneOf")
    data class OneOf(
        @SerialName("oneOf") val variants: List<OpenApiSchema> = emptyList(),
        override val title: kotlin.String = "",
        override val nullable: kotlin.Boolean = false,
        override val deprecated: kotlin.Boolean = false
    ) : OpenApiSchema

}
