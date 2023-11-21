package dev.h4kt.ktorDocs.types.parameters.impl

import dev.h4kt.ktorDocs.types.parameters.RouteParameter
import io.ktor.http.*
import io.ktor.server.util.*
import io.ktor.util.reflect.*

data class StringRouteParameter(
    override val name: String,
    override val description: String,
    override val optional: Boolean
) : RouteParameter<String>() {

    override val typeInfo = typeInfo<String>()

    override fun parse(parameters: Parameters) {
        value = parameters.getOrFail(name)
    }

}
