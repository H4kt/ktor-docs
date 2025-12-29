package dev.h4kt.ktorDocs.generation.inputs

import kotlinx.serialization.SerialName

@Suppress("UNUSED")
sealed class TestSealedClass {
    @SerialName("FOO")
    data object Foo : TestSealedClass()

    @SerialName("BAR")
    data object Bar : TestSealedClass()
}
