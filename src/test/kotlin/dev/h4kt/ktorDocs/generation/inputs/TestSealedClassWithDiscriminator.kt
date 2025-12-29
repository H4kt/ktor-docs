package dev.h4kt.ktorDocs.generation.inputs

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonClassDiscriminator

@Suppress("UNUSED")
@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("version")
sealed class TestSealedClassWithDiscriminator {
    @SerialName("1")
    data object V1 : TestSealedClassWithDiscriminator()

    @SerialName("2")
    data object V2 : TestSealedClassWithDiscriminator()
}
