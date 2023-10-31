import dev.h4kt.ktorDocs.dsl.get
import dev.h4kt.ktorDocs.types.parameters.RouteParameters
import io.ktor.server.routing.*

fun Routing.configureTestRouting() {

    class GetQueryParams : RouteParameters() {
        val name by string {
            name = "name"
            description = "A random name parameter"
        }
    }

    get("", ::GetQueryParams, ::GetQueryParams) {

        description = ""

        handle {

        }

    }

}
