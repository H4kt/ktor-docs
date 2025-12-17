package dev.h4kt.ktorDocs.generation.converters.auth

import dev.h4kt.ktorDocs.annotations.UnsafeAPI
import dev.h4kt.ktorDocs.mock.MockPipelineCall
import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSecurityScheme
import dev.h4kt.ktorDocs.utils.getInternalField
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.AuthenticationProvider
import io.ktor.server.auth.OAuthAuthenticationProvider
import io.ktor.server.auth.OAuthServerSettings

class OAuthAuthProviderConverter : AuthProviderConverter(priority = 100) {

    override fun canConvert(
        provider: AuthenticationProvider
    ): Boolean {
        return provider is OAuthAuthenticationProvider
    }

    @OptIn(UnsafeAPI::class)
    override fun convert(
        provider: AuthenticationProvider,
        application: Application
    ): OpenApiSecurityScheme {
        require(provider is OAuthAuthenticationProvider)

        val call = MockPipelineCall(application)
        val providerLookup = provider.getInternalField<ApplicationCall.() -> OAuthServerSettings?>("providerLookup")

        val serverSettings = providerLookup(call) as? OAuthServerSettings.OAuth2ServerSettings
            ?: error("Failed to find server settings for $provider")

        return OpenApiSecurityScheme.OAuth2(
            flows = OpenApiSecurityScheme.OAuth2.Flows(
                authorizationCode = OpenApiSecurityScheme.OAuth2.Flow(
                    authorizationUrl = serverSettings.authorizeUrl,
                    tokenUrl = serverSettings.accessTokenUrl,
                    scopes = serverSettings.defaultScopes.associateWith { "" },
                )
            )
        )
    }

}
