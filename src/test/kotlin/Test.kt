import dev.h4kt.ktorDocs.dsl.get
import dev.h4kt.ktorDocs.extensions.absolutePath
import dev.h4kt.ktorDocs.plugin.KtorDocs
import dev.h4kt.ktorDocs.types.parameters.RouteParameters
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MainTest {

    @Test
    fun testWrapping() = testApplication {

        routing {

            class PathParams : RouteParameters() {
                val name by string {
                    name = "name"
                    description = "A random path parameter"
                }
            }

            class QueryParams : RouteParameters() {
                val surname by string {
                    name = "surname"
                    description = "A random query parameter"
                }
            }

            get("{name}", ::PathParams, ::QueryParams) {

                description = "A random test request"

                responses {
                    HttpStatusCode.OK returns String
                }

                handle {
                    call.respond("${pathParameters.name} ${queryParameters.surname}")
                }

            }

        }

        install(KtorDocs)

        val response = client.get("foo") {
            parameter("surname", "bar")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("foo bar", response.bodyAsText())

    }

    @Test
    fun testRouteAbsolutePathResolution() = testApplication {

        routing {

            assertEquals("/", get {}.absolutePath)

            route("foo") {
                route("bar") {
                    assertEquals("/foo/bar/buz", get("buz") {}.absolutePath)
                }
            }

        }

    }

}
