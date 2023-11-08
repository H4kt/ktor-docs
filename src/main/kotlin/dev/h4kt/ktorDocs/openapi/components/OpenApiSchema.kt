package dev.h4kt.ktorDocs.openapi.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class OpenApiSchema private constructor() {

    abstract val nullable: kotlin.Boolean
    abstract val deprecated: kotlin.Boolean

    @Serializable
    data class Reference(
        @SerialName("\$ref") val ref: kotlin.String,
        override val nullable: kotlin.Boolean = false,
        override val deprecated: kotlin.Boolean = false,
    ) : OpenApiSchema()

    @Serializable
    @SerialName("string")
    data class String(
        val format: kotlin.String? = null,
        val pattern: kotlin.String? = null,
        val enum: List<String>? = null,
        override val nullable: kotlin.Boolean = false,
        override val deprecated: kotlin.Boolean = false,
    ) : OpenApiSchema()

    @Serializable
    @SerialName("number")
    data class Number(
        val minimum: Int? = null,
        val exclusiveMinimum: Boolean? = null,
        val maximum: Int? = null,
        val exclusiveMaximum: Boolean? = null,
        val format: Format? = null,
        override val nullable: kotlin.Boolean = false,
        override val deprecated: kotlin.Boolean = false,
    ) : OpenApiSchema() {

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
        override val nullable: kotlin.Boolean = false,
        override val deprecated: kotlin.Boolean = false,
    ) : OpenApiSchema() {

        enum class Format {
            INT32, INT64
        }

    }

    @Serializable
    @SerialName("boolean")
    data class Boolean(
        override val nullable: kotlin.Boolean = false,
        override val deprecated: kotlin.Boolean = false,
    ) : OpenApiSchema()

    @Serializable
    @SerialName("array")
    data class Array(
        val items: OpenApiSchema,
        override val nullable: kotlin.Boolean = false,
        override val deprecated: kotlin.Boolean = false,
    ) : OpenApiSchema()

    @Serializable
    @SerialName("object")
    data class Object(
        val properties: Map<kotlin.String, OpenApiSchema> = emptyMap(),
        val required: List<kotlin.String> = emptyList(),
        override val nullable: kotlin.Boolean = false,
        override val deprecated: kotlin.Boolean = false,
    ) : OpenApiSchema()

}
