package dev.h4kt.ktorDocs

import dev.h4kt.ktorDocs.annotations.UnsafeAPI
import dev.h4kt.ktorDocs.mock.MockPipelineCall
import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSecurityScheme
import dev.h4kt.ktorDocs.utils.getInternalField
import io.ktor.server.application.*
import io.ktor.server.auth.*

fun AuthenticationProvider.toOpenApiSecurityScheme(
    application: Application
): OpenApiSecurityScheme? {
    return when (this) {
        is BasicAuthenticationProvider -> toOpenApiBasicSecurityScheme()
        is BearerAuthenticationProvider -> toOpenApiBearerSecurityScheme()
        is OAuthAuthenticationProvider -> toOpenApiOAuth2SecurityScheme(application)
        else -> null
    }
}

private fun BasicAuthenticationProvider.toOpenApiBasicSecurityScheme(): OpenApiSecurityScheme {
    return OpenApiSecurityScheme.Http(
        scheme = OpenApiSecurityScheme.Http.Scheme.BASIC
    )
}

private fun BearerAuthenticationProvider.toOpenApiBearerSecurityScheme(): OpenApiSecurityScheme {
    return OpenApiSecurityScheme.Http(
        scheme = OpenApiSecurityScheme.Http.Scheme.BEARER
    )
}

@OptIn(UnsafeAPI::class)
private fun OAuthAuthenticationProvider.toOpenApiOAuth2SecurityScheme(
    application: Application
): OpenApiSecurityScheme? {

    val call = MockPipelineCall(application)
    val providerLookup = getInternalField<ApplicationCall.() -> OAuthServerSettings?>("providerLookup")

    val serverSettings = providerLookup(call) as? OAuthServerSettings.OAuth2ServerSettings
        ?: return null

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
