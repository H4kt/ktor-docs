package dev.h4kt.ktorDocs._v2.types.route.response

import kotlin.reflect.KType

sealed interface ResponseBody {

    val description: String

    data class Schema(
        val type: KType,
        override val description: String = ""
    ) : ResponseBody

}
