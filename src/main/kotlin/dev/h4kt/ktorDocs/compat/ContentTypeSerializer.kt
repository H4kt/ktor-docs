package dev.h4kt.ktorDocs.compat

import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

typealias SerialContentType = @Serializable(with = ContentTypeSerializer::class) ContentType

object ContentTypeSerializer : KSerializer<ContentType> {

    override val descriptor = PrimitiveSerialDescriptor(
        serialName = "ContentType",
        kind = PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): ContentType {
        return ContentType.parse(decoder.decodeString())
    }

    override fun serialize(
        encoder: Encoder,
        value: ContentType
    ) {
        encoder.encodeString("${value.contentType}/${value.contentSubtype}")
    }

}
