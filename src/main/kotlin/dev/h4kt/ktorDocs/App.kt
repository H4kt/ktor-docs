package dev.h4kt.ktorDocs

import dev.h4kt.ktorDocs.dsl.post
import dev.h4kt.ktorDocs.plugin.KtorDocs
import dev.h4kt.ktorDocs.plugin.KtorDocsConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import kotlinx.serialization.SerialName

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

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

fun Application.module() {

    routing {

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

    install(KtorDocs) {
        openApi {
            info {
                title = "Beer API"
            }
        }
    }

}
