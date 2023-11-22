package dev.h4kt.ktorDocs.types.parameters.parsers.datetime

import dev.h4kt.ktorDocs.types.parameters.parsers.ParameterParser
import io.ktor.http.*
import kotlinx.datetime.LocalDateTime

object LocalDateTimeParameterParser : ParameterParser<LocalDateTime> {

    override fun parse(
        parameters: Parameters,
        name: String
    ): LocalDateTime? = parameters[name]?.run(LocalDateTime::parse)

}
