package dev.h4kt.ktorDocs.types.parameters.parsers.datetime

import dev.h4kt.ktorDocs.types.parameters.parsers.ParameterParser
import io.ktor.http.*
import kotlinx.datetime.LocalDate

object LocalDateParameterParser : ParameterParser<LocalDate> {

    override fun parse(
        parameters: Parameters,
        name: String
    ): LocalDate? = parameters[name]?.run(LocalDate::parse)

}
