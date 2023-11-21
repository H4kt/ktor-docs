package dev.h4kt.ktorDocs.types.parameters.impl

import dev.h4kt.ktorDocs.types.parameters.RouteParameter
import io.ktor.http.*
import io.ktor.server.util.*
import io.ktor.util.reflect.*
import kotlinx.datetime.LocalDateTime

data class LocalDateTimeRouteParameter(
    override val name: String,
    override val description: String,
    override val optional: Boolean
) : RouteParameter<LocalDateTime>() {

    override val type = typeInfo<LocalDateTime>()

    override fun parse(parameters: Parameters) {
        value = parameters.getOrFail<String>(name).run(LocalDateTime::parse)
    }

}
