package dev.h4kt.ktorDocs.types.parameters.parsers.datetime

import dev.h4kt.ktorDocs.types.parameters.parsers.ParameterParser
import io.ktor.http.*
import kotlin.time.Duration

object DurationParameterParser : ParameterParser<Duration> {

    override fun parse(
        parameters: Parameters,
        name: String
    ): Duration? = parameters[name]?.run(Duration::parseIsoString)

}
