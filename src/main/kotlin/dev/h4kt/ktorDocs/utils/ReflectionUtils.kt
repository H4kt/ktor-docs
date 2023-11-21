package dev.h4kt.ktorDocs.utils

import dev.h4kt.ktorDocs.annotations.UnsafeAPI

@UnsafeAPI
internal inline fun <reified T> Any.getInternalField(name: String): T {

    val field = this::class.java.getDeclaredField(name)
        .apply {
            isAccessible = true
        }

    return field.get(this) as T
}
