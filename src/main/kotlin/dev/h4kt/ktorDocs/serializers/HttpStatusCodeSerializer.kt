package dev.h4kt.ktorDocs.serializers

import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

typealias SerialHttpStatusCode = @Serializable(with = HttpStatusCodeSerializer::class) HttpStatusCode

object HttpStatusCodeSerializer : KSerializer<HttpStatusCode> {

    override val descriptor = PrimitiveSerialDescriptor(
        serialName = "HttpStatusCode",
        kind = PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): HttpStatusCode {
        return HttpStatusCode.fromValue(decoder.decodeString().toInt())
    }

    override fun serialize(
        encoder: Encoder,
        value: HttpStatusCode
    ) {
        encoder.encodeString(value.value.toString())
    }

}
