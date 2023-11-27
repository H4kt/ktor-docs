import dev.h4kt.ktorDocs.dsl.get
import dev.h4kt.ktorDocs.plugin.KtorDocs
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestWrapping {

    @Test
    fun testGeneration() = testApplication {

        install(KtorDocs) {
            openApi {
                info {
                    title = "Beer API"
                }
            }
        }

        routing {
            route("foo") {
                route("bar") {
                    get {
                        handle {
                            call.respond(HttpStatusCode.NoContent)
                        }
                    }
                }
            }
        }

        val response = client.get("foo/bar")

        assertEquals(HttpStatusCode.NoContent, response.status)

    }

}
