package dev.h4kt.ktorDocs._v2.types.parameters.parsers.primitives

import dev.h4kt.ktorDocs.types.parameters.parsers.ParameterParser
import io.ktor.http.*

object LongParameterParser : ParameterParser<Long> {

    override fun parse(
        parameters: Parameters,
        name: String
    ): Long? = parameters[name]?.toLong()

}
