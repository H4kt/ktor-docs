package dev.h4kt.ktorDocs.types.parameters.impl

import dev.h4kt.ktorDocs.types.parameters.RouteParameter
import io.ktor.http.*
import io.ktor.server.util.*
import io.ktor.util.reflect.*
import kotlinx.datetime.LocalDate

data class LocalDateRouteParameter(
    override val name: String,
    override val description: String,
    override val optional: Boolean
) : RouteParameter<LocalDate>() {

    override val type = typeInfo<LocalDate>()

    override fun parse(parameters: Parameters) {
        value = parameters.getOrFail<String>(name).run(LocalDate::parse)
    }

}
