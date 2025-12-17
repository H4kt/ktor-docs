import dev.h4kt.ktorDocs.dsl.get
import dev.h4kt.ktorDocs.plugin.KtorDocs
import io.ktor.server.testing.*
import io.ktor.util.reflect.*
import org.junit.jupiter.api.Test

data class Foo<T : Any>(
    val bar: Bar<T>,
    val bar2: Bar<String>
)

data class Bar<D : Any>(
    val value: List<D>
)

class TestGenerics {

    @Test
    fun testGenerics() = testApplication {

        install(KtorDocs)

        routing {
            get {
                requestBody = typeInfo<Foo<Int>>()
            }
        }

    }

}
