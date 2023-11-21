package dev.h4kt.ktorDocs.types.parameters.impl

import dev.h4kt.ktorDocs.types.parameters.RouteParameter
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.util.*
import io.ktor.util.reflect.*
import kotlin.time.Duration

data class DurationRouteParameter(
    override val name: String,
    override val description: String,
    override val optional: Boolean
) : RouteParameter<Duration>() {

    override val typeInfo = typeInfo<Duration>()

    override fun parse(parameters: Parameters) {

        try {
            value = parameters.getOrFail<String>(name).run(Duration::parse)
        } catch (ex: Exception) {
            throw ParameterConversionException(
                parameterName = name,
                type = typeInfo.type.simpleName ?: typeInfo.type.toString(),
                cause = ex
            )
        }

    }

}
