import dev.h4kt.ktorDocs.dsl.get
import dev.h4kt.ktorDocs.dsl.post
import dev.h4kt.ktorDocs.plugin.KtorDocs
import dev.h4kt.ktorDocs.types.parameters.RouteParameters
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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

class Foo<T : Any>(
    val bar: Bar<T>,
    val bar2: Bar<String>
)

class Bar<T : Any>(
    val value: T
)

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

                route("beers") {

                    val beers = mutableListOf<Beer>()

                    class GetRouteParameters : RouteParameters() {
                        val kind by query.optionalEnum<Beer.Leffe.Kind> {
                            name = "kind"
                            description = "Filter by kind"
                        }
                    }

                    get("leffe", ::GetRouteParameters) {

                        description = "Get list of known leffe beers"

                        responses {
                            HttpStatusCode.OK returns typeInfo<List<Beer>>()
                        }

                        handle {

                            val results = synchronized(beers) {
                                beers.filterIsInstance<Beer.Leffe>()
                                    .run {
                                        parameters.kind?.let { kind ->
                                            filter { it.kind == kind }
                                        } ?: this
                                    }
                            }

                            call.respond(results)
                        }
                    }

                    post("new") {

                        description = "Add new beer"
                        tags = listOf("Beer")

                        requestBody = typeInfo<Foo<Int>>()

                        responses {
                            noContent()
                        }

                        handle {

                            val beer = call.receive<Beer>()

                            synchronized(beers) {
                                beers += beer
                            }

                            call.respond(HttpStatusCode.NoContent)
                        }
                    }

                }

            }

        }

    }

}
