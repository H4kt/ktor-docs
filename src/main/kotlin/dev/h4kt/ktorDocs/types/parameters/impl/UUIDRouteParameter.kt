package dev.h4kt.ktorDocs.types.parameters.impl

import dev.h4kt.ktorDocs.types.parameters.RouteParameter
import io.ktor.http.*
import io.ktor.server.util.*
import io.ktor.util.reflect.*
import java.util.*

data class UUIDRouteParameter(
    override val name: String,
    override val description: String,
    override val optional: Boolean
) : RouteParameter<UUID>() {

    override val typeInfo = typeInfo<UUID>()

    override fun parse(parameters: Parameters) {
        value = parameters.getOrFail<UUID>(name)
    }

}
