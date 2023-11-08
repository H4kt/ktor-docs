package dev.h4kt.ktorDocs.openapi.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class OpenApiSecurityScheme {

    @Serializable
    @SerialName("http")
    data class Http(
        val scheme: Scheme
    ) {

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
    ) {

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
    )

    @Serializable
    @SerialName("oauth2")
    data class OAuth2(
        val flows: Flows
    ) {

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
