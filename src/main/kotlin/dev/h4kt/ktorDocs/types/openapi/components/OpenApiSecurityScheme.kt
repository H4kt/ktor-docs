package dev.h4kt.ktorDocs.types.openapi.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface OpenApiSecurityScheme {

    @Serializable
    @SerialName("http")
    data class Http(
        val scheme: Scheme
    ) : OpenApiSecurityScheme {

        @Serializable
        enum class Scheme {
            @SerialName("basic") BASIC,
            @SerialName("bearer") BEARER
        }

    }

    @Serializable
    @SerialName("apiKey")
    data class ApiKey(
        val disposition: Disposition,
        val name: String,
    ) : OpenApiSecurityScheme {

        @Serializable
        enum class Disposition {
            @SerialName("header") HEADER,
            @SerialName("query") QUERY
        }

    }

    @Serializable
    @SerialName("openIdConnect")
    data class OpenId(
        val openIdConnectUrl: String
    ) : OpenApiSecurityScheme

    @Serializable
    @SerialName("oauth2")
    data class OAuth2(
        val flows: Flows
    ) : OpenApiSecurityScheme {

        @Serializable
        data class Flows(
            val authorizationCode: Flow
        )

        @Serializable
        data class Flow(
            val authorizationUrl: String,
            val tokenUrl: String,
            val scopes: Map<String, String>
        )

    }

}
