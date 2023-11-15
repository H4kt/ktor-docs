package dev.h4kt.ktorDocs.serializers

//import com.charleskorn.kaml.YamlInput
//import com.charleskorn.kaml.YamlMap
//import com.charleskorn.kaml.YamlNode
//import dev.h4kt.ktorDocs.extensions.contains
//import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
//import dev.h4kt.ktorDocs.types.openapi.components.TypedSchema
//import kotlinx.serialization.*
//import kotlinx.serialization.descriptors.SerialKind
//import kotlinx.serialization.descriptors.buildSerialDescriptor
//import kotlinx.serialization.encoding.Decoder
//import kotlinx.serialization.encoding.Encoder
//
//@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
//object OpenApiSchemaSerializer : KSerializer<OpenApiSchema> {
//
//    override val descriptor = buildSerialDescriptor("OpenApiSchema", SerialKind.CONTEXTUAL)
//
//    override fun serialize(
//        encoder: Encoder,
//        value: OpenApiSchema
//    ) {
//
//        val actualSerializer =
//            encoder.serializersModule.getPolymorphic(OpenApiSchema::class, value)
//                ?: value::class.serializerOrNull()
////                ?: throwSubtypeNotRegistered(value::class, baseClass)
//                ?: throw Exception()
//
//        @Suppress("UNCHECKED_CAST")
//        (actualSerializer as KSerializer<OpenApiSchema>).serialize(encoder, value)
//    }
//
//    override fun deserialize(decoder: Decoder): OpenApiSchema {
//        val input = decoder.beginStructure(descriptor) as YamlInput
//        return selectDeserializer(input.node).deserialize(input)
//    }
//
//    private fun selectDeserializer(
//        element: YamlNode
//    ): DeserializationStrategy<OpenApiSchema> {
//
//        if (element !is YamlMap) {
//            throw Exception("OpenApiSchema must be an object")
//        }
//
//        return when {
//            "\$ref" in element -> OpenApiSchema.Reference.serializer()
//            "oneOf" in element -> OpenApiSchema.OneOf.serializer()
//            else -> TypedSchema.serializer()
//        }
//    }
//
//}
