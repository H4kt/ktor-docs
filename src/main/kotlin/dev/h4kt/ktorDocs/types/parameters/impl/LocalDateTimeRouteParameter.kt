package dev.h4kt.ktorDocs.types.parameters.impl

import dev.h4kt.ktorDocs.types.parameters.RouteParameter
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.util.*
import io.ktor.util.reflect.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class LocalDateTimeRouteParameter(
    override val name: String,
    override val description: String,
    override val required: Boolean
) : RouteParameter<LocalDateTime>() {

    override val typeInfo = typeInfo<LocalDateTime>()

    override fun parse(parameters: Parameters) {

        try {
            value = parameters.getOrFail<String>(name).run(LocalDateTime::parse)
        } catch (ex: Exception) {
            throw ParameterConversionException(
                parameterName = name,
                type = typeInfo.type.simpleName ?: typeInfo.type.toString(),
                cause = ex
            )
        }

    }

}
