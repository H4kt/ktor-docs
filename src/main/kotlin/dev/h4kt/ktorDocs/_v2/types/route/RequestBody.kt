package dev.h4kt.ktorDocs._v2.types.route

import kotlin.reflect.KType

sealed interface RequestBody {

    data class Schema(
        val type: KType
    ) : RequestBody

}
