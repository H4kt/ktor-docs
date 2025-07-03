@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package dev.h4kt.ktorDocs._v2.types.parameters.parsers

import io.ktor.http.Parameters
import kotlin.reflect.KClass
import java.lang.Enum as JavaEnum

class EnumParameterParser<T : Enum<T>>(
    private val kclass: KClass<T>
) : ParameterParser<T> {

    override fun parse(
        parameters: Parameters,
        name: String
    ): T? {
        return parameters[name]
            ?.let {
                JavaEnum.valueOf(kclass.java, it)
            }
    }

}
