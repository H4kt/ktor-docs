package dev.h4kt.ktorDocs.types.parameters.impl

import dev.h4kt.ktorDocs.types.parameters.RouteParameter
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.util.*
import io.ktor.util.reflect.*
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class InstantRouteParameter(
    override val name: String,
    override val description: String,
    override val required: Boolean
) : RouteParameter<Instant>() {

    override val typeInfo = typeInfo<Instant>()

    override fun parse(parameters: Parameters) {

        try {
            value = parameters.getOrFail<String>(name).run(Instant::parse)
        } catch (ex: Exception) {
            throw ParameterConversionException(
                parameterName = name,
                type = typeInfo.type.simpleName ?: typeInfo.type.toString(),
                cause = ex
            )
        }

    }

}
