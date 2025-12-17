package dev.h4kt.ktorDocs.generation

import kotlin.reflect.KClass
import kotlin.reflect.KTypeProjection

data class TypeMapKey(
    val kClass: KClass<*>,
    val arguments: List<KTypeProjection>
)
