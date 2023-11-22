package dev.h4kt.ktorDocs.types.parameters.parsers

import io.ktor.http.*

interface ParameterParser<TOutput : Any> {

    fun parse(
        parameters: Parameters,
        name: String
    ): TOutput?

}
