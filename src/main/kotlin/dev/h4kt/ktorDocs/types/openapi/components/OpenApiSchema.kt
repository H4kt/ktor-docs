package dev.h4kt.ktorDocs.types.openapi.components

import dev.h4kt.ktorDocs.serializers.OpenApiSchemaSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = OpenApiSchemaSerializer::class)
sealed interface OpenApiSchema {

    val title: kotlin.String
    val nullable: kotlin.Boolean
    val deprecated: kotlin.Boolean

    @Serializable
    sealed interface TypedSchema : OpenApiSchema

    @Serializable
    @SerialName("string")
    data class String(
        val format: kotlin.String? = null,
        val pattern: kotlin.String? = null,
        val enum: List<kotlin.String>? = null,
        override val title: kotlin.String = "",
        override val nullable: kotlin.Boolean = false,
        override val deprecated: kotlin.Boolean = false
    ) : TypedSchema

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
    ) : TypedSchema {

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
    ) : TypedSchema {

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
    ) : TypedSchema

    @Serializable
    @SerialName("array")
    data class Array(
        val items: OpenApiSchema,
        val uniqueItems: kotlin.Boolean = false,
        override val title: kotlin.String = "",
        override val nullable: kotlin.Boolean = false,
        override val deprecated: kotlin.Boolean = false
    ) : TypedSchema

    @Serializable
    @SerialName("object")
    data class Object(
        val properties: Map<kotlin.String, OpenApiSchema> = emptyMap(),
        val required: List<kotlin.String> = emptyList(),
        override val title: kotlin.String = "",
        override val nullable: kotlin.Boolean = false,
        override val deprecated: kotlin.Boolean = false
    ) : TypedSchema

    @Serializable
    data class Reference(
        @SerialName("\$ref") val ref: kotlin.String,
        override val title: kotlin.String = "",
        override val nullable: kotlin.Boolean = false,
        override val deprecated: kotlin.Boolean = false
    ) : OpenApiSchema

    @Serializable
    data class OneOf(
        @SerialName("oneOf") val variants: List<OpenApiSchema> = emptyList(),
        override val title: kotlin.String = "",
        override val nullable: kotlin.Boolean = false,
        override val deprecated: kotlin.Boolean = false
    ) : OpenApiSchema

}
