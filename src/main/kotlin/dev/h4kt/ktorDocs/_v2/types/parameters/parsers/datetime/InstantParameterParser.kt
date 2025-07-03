package dev.h4kt.ktorDocs._v2.types.parameters.parsers.datetime

import dev.h4kt.ktorDocs.types.parameters.parsers.ParameterParser
import io.ktor.http.*
import kotlinx.datetime.Instant

object InstantParameterParser : ParameterParser<Instant> {

    override fun parse(
        parameters: Parameters,
        name: String
    ): Instant? = parameters[name]?.run(Instant::parse)

}
