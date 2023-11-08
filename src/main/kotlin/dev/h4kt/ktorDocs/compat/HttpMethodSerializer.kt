package dev.h4kt.ktorDocs.compat

import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

typealias SerialHttpMethod = @Serializable(with = HttpMethodSerializer::class) HttpMethod

object HttpMethodSerializer : KSerializer<HttpMethod> {

    override val descriptor = PrimitiveSerialDescriptor(
        serialName = "HttpMethod",
        kind = PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): HttpMethod {
        return HttpMethod(decoder.decodeString().uppercase())
    }

    override fun serialize(
        encoder: Encoder,
        value: HttpMethod
    ) {
        encoder.encodeString(value.value.lowercase())
    }

}
