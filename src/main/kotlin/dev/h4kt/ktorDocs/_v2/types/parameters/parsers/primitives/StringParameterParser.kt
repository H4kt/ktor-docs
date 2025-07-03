package dev.h4kt.ktorDocs._v2.types.parameters.parsers.primitives

import dev.h4kt.ktorDocs.types.parameters.parsers.ParameterParser
import io.ktor.http.*

object StringParameterParser : ParameterParser<String> {

    override fun parse(
        parameters: Parameters,
        name: String
    ): String? = parameters[name]

}
