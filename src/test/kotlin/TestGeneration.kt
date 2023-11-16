import dev.h4kt.ktorDocs.dsl.post
import dev.h4kt.ktorDocs.plugin.KtorDocs
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.testing.*
import io.ktor.util.reflect.*
import kotlinx.serialization.SerialName
import org.junit.jupiter.api.Test

sealed interface Beer {

    @SerialName("LEFFE")
    data class Leffe(
        val kind: Kind
    ) : Beer {
        enum class Kind {
            BRUNE, BLONDE, RUBY
        }
    }

    @SerialName("MILLER")
    data class Miller(
        val isCanned: Boolean
    ) : Beer

}

class TestGeneration {

    @Test
    fun testGeneration() = testApplication {

        install(KtorDocs) {
            openApi {
                info {
                    title = "Beer API"
                }
            }
        }

        install(Authentication) {

            basic("test-basic") {
                realm = "Ktor Server"
                validate { credentials ->
                    if (credentials.name == credentials.password) {
                        UserIdPrincipal(credentials.name)
                    } else {
                        null
                    }
                }
            }

            bearer("test-bearer") {
                authenticate { null }
            }

            oauth("test-oauth") {
                urlProvider = { "https://localhost:1337/foo" }
                providerLookup = {
                    OAuthServerSettings.OAuth2ServerSettings(
                        name = "google",
                        authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                        accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                        requestMethod = HttpMethod.Post,
                        clientId = "foo",
                        clientSecret = "bar",
                        defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile"),
                        extraAuthParameters = listOf("access_type" to "offline")
                    )
                }
                client = HttpClient()
            }

        }

        routing {

            authenticate("test-basic") {
                post("/beer/new") {

                    description = "Add new beer"
                    tags = listOf("Beer")

                    requestBody = typeInfo<Beer>()

                    responses {
                        HttpStatusCode.OK returns typeInfo<Beer>()
                    }

                    handle {
                        TODO()
                    }

                }
            }

        }

    }

}
