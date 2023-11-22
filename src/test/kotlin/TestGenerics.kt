import dev.h4kt.ktorDocs.dsl.get
import dev.h4kt.ktorDocs.plugin.KtorDocs
import io.ktor.server.testing.*
import io.ktor.util.reflect.*
import org.junit.jupiter.api.Test

class Foo<T : Any>(
    val bar: Bar<T>,
    val bar2: Bar<String>
)

class Bar<T : Any>(
    val value: List<T>
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
