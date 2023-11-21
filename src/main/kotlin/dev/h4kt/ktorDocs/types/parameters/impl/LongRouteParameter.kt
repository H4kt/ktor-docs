package dev.h4kt.ktorDocs.types.parameters.impl

import dev.h4kt.ktorDocs.types.parameters.RouteParameter
import io.ktor.http.*
import io.ktor.server.util.*
import io.ktor.util.reflect.*

data class LongRouteParameter(
    override val name: String,
    override val description: String,
    override val optional: Boolean
) : RouteParameter<Long>() {

    override val typeInfo = typeInfo<Long>()

    override fun parse(parameters: Parameters) {
        value = parameters.getOrFail<Long>(name)
    }

}
