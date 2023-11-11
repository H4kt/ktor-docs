package dev.h4kt.ktorDocs.plugin

import com.charleskorn.kaml.*
import dev.h4kt.ktorDocs.types.openapi.OpenApiServer
import dev.h4kt.ktorDocs.types.openapi.OpenApiSpec
import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRoute
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRouteBody
import dev.h4kt.ktorDocs.types.openapi.route.OpenApiRouteParameter
import io.ktor.http.*
import io.ktor.util.reflect.*
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

private val builtInTypes = mapOf(
    String::class to OpenApiSchema.String(),
    Char::class to OpenApiSchema.String(),
    Int::class to OpenApiSchema.Integer(
        format = OpenApiSchema.Integer.Format.INT32
    ),
    Short::class to OpenApiSchema.Integer(
        minimum = Short.MIN_VALUE.toInt(),
        maximum = Short.MAX_VALUE.toInt(),
    ),
    Byte::class to OpenApiSchema.Integer(
        minimum = Byte.MIN_VALUE.toInt(),
        maximum = Byte.MAX_VALUE.toInt(),
    ),
    Long::class to OpenApiSchema.Integer(
        format = OpenApiSchema.Integer.Format.INT64
    ),
    Boolean::class to OpenApiSchema.Boolean(),
)

class User(
    val name: String,
    val role: Role
) {

    enum class Role {
        DEFAULT,
        ADMIN
    }

}

fun main() {

//    class AuthResponse(
//        val user: User
//    )

    class UsersResponse(
        val users: List<User>
    )

    val responseSchema = typeInfo<UsersResponse>().kotlinType!!.toOpenApiSchema()

    val spec = OpenApiSpec(
        version = "3.0.0",
        info = OpenApiSpec.Info(
            title = "Test API docs",
            version = "v1",
            description = "Dummy description"
        ),
        servers = listOf(
            OpenApiServer(
                url = "http://localhost:1337",
                description = "Local test server"
            )
        ),
        tags = listOf(
            OpenApiSpec.Tag("common")
        ),
        paths = mapOf(
            "/foo/bar" to mapOf(
                HttpMethod.Get to OpenApiRoute(
                    tags = listOf("common"),
                    summary = "Test API route",
                    parameters = listOf(
                        OpenApiRouteParameter(
                            type = OpenApiRouteParameter.Type.QUERY,
                            name = "query",
                            schema = OpenApiSchema.String()
                        )
                    ),
                    responses = mapOf(
                        "200" to OpenApiRouteBody(
                            description = "Success",
                            content = mapOf(
                                ContentType.Application.Json to OpenApiRouteBody.Schema(
                                    schema = responseSchema
                                )
                            )
                        )
                    )
                )
            )
        )
    )

    val yaml = Yaml(
        configuration = YamlConfiguration(
            encodeDefaults = false,
            polymorphismStyle = PolymorphismStyle.Property,
            singleLineStringStyle = SingleLineStringStyle.Plain,
            sequenceBlockIndent = 2
        )
    )

    val file = File("documentation.yml")

    file.outputStream().use {
        yaml.encodeToStream(spec, it)
    }

}

fun KType.toOpenApiSchema(): OpenApiSchema {

    val type = this.classifier as? KClass<*>
        ?: throw UnsupportedOperationException()

    return when {
        type in builtInTypes ->
            builtInTypes[type]!!
//            OpenApiSchema.Reference("#/components/schemas/${qualifiedName}")
        type.isSubclassOf(Enum::class) -> type.toOpenApiEnum()
        type.isSubclassOf(List::class) -> {
            OpenApiSchema.Array(
                items = arguments.first().type!!.toOpenApiSchema()
            )
        }
        else -> toOpenApiObject()
    }

}

@Suppress("UNCHECKED_CAST")
private fun KClass<*>.toOpenApiEnum(): OpenApiSchema.String {

    val values = (java as Class<Enum<*>>).enumConstants
        .map { it.name }

    return OpenApiSchema.String(
        enum = values
    )
}

private fun KType.toOpenApiObject(): OpenApiSchema.Object {

    val type = this.classifier as? KClass<*>
        ?: throw UnsupportedOperationException()

    val properties = type.memberProperties
        .associateBy(
            keySelector = { it.name },
            valueTransform = { it.returnType.toOpenApiSchema() }
        )

    val required = type.primaryConstructor
        ?.parameters
        ?.filter { !it.isOptional }
        ?.mapNotNull { it.name }
        ?: emptyList()

    return OpenApiSchema.Object(
        properties = properties,
        required = required
    )
}

//private fun DocumentedRoute.toOpenApiRoute(): OpenApiRoute {
//    return OpenApiRoute()
//}
//
//private val builtInTypes = listOf(
//    String::class,
//    Char::class,
//    Int::class,
//    Short::class,
//    Byte::class,
//    Long::class,
//    Boolean::class,
//)
//
//private fun TypeInfo.toOpenApiSchema(): OpenApiSchema {
//
//    type.typeParameters
//    when (type) {
//    }
//
//}

//data class Foo(
//    val value: List<String>
//)
//
//fun main() {
//
//    val (type) = typeInfo<Foo>()
//    val valueProperty = type.memberProperties.first()
//
//    println(valueProperty.returnType.arguments.first())
//
//}
