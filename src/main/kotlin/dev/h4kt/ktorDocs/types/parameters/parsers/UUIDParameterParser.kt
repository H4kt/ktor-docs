package dev.h4kt.ktorDocs.types.parameters.parsers

import io.ktor.http.*
import java.util.*

object UUIDParameterParser : ParameterParser<UUID> {

    override fun parse(
        parameters: Parameters,
        name: String
    ): UUID? = parameters[name]?.run(UUID::fromString)

}
