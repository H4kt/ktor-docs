package dev.h4kt.ktorDocs.types.parameters.parsers.primitives

import dev.h4kt.ktorDocs.types.parameters.parsers.ParameterParser
import io.ktor.http.*

object IntParameterParser : ParameterParser<Int> {

    override fun parse(
        parameters: Parameters,
        name: String
    ): Int? = parameters[name]?.toInt()

}
