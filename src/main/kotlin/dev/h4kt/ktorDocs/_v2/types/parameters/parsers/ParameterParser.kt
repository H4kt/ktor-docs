package dev.h4kt.ktorDocs._v2.types.parameters.parsers

import io.ktor.http.Parameters

interface ParameterParser<TOutput : Any> {

    fun parse(
        parameters: Parameters,
        name: String
    ): TOutput?

}
