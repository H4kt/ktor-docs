package dev.h4kt.ktorDocs.generation.converters.auth

import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSecurityScheme
import io.ktor.server.application.Application
import io.ktor.server.auth.AuthenticationProvider
import io.ktor.server.auth.BasicAuthenticationProvider
import io.ktor.server.auth.BearerAuthenticationProvider

class BearerAuthProviderConverter : AuthProviderConverter(priority = 100) {

    override fun canConvert(
        provider: AuthenticationProvider
    ): Boolean {
        return provider is BearerAuthenticationProvider
    }

    override fun convert(
        provider: AuthenticationProvider,
        application: Application
    ): OpenApiSecurityScheme {
        require(provider is BearerAuthenticationProvider)
        return OpenApiSecurityScheme.Http(scheme = BEARER)
    }

}
