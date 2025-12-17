package dev.h4kt.ktorDocs.generation.converters.auth

import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSecurityScheme
import io.ktor.server.application.Application
import io.ktor.server.auth.AuthenticationProvider

abstract class AuthProviderConverter(
    val priority: Int
) {

    abstract fun canConvert(
        provider: AuthenticationProvider
    ): Boolean

    abstract fun convert(
        provider: AuthenticationProvider,
        application: Application
    ): OpenApiSecurityScheme

}
