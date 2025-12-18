# ktor-docs
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/H4kt/ktor-docs/deploy.yml?style=for-the-badge)
![Kotlin](https://img.shields.io/badge/kotlin-2.3.0-purple?style=for-the-badge&logo=kotlin&label=Kotlin&color=%237F52FF)
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.h4kt.dev%2Freleases%2Fdev%2Fh4kt%2Fktor-docs%2Fmaven-metadata.xml&style=for-the-badge&logo=apache-maven&color=%237F52FF)

## ⚠️ Deprecation notice ⚠️
This project is now superseded by the [official way](https://ktor.io/docs/openapi-spec-generation.html) of generating OpenAPI docs

## Usage

### Add dependencies
build.gradle.kts
```kotlin
repositories {
    maven("https://repo.h4kt.dev/releases")
}

dependencies {
    implementation("dev.h4kt:ktor-docs:2.0.0")
}
```

### Plugin installation
```kotlin
fun Application.module() {

    install(KtorDocs) {
    
        openApi {

            version = "3"
    
            info {
                version = "1"
                title = "Template project docs"
            }
    
            server {
                url = "http://localhost:1337"
                description = "Local development server"
            }

        }

        swagger {
            path = "/docs"
        }
        
    }
    
}
```

### Usage
In order for your route to be included in the resulting specification it has to be defined using a custom [extension function](https://github.com/H4kt/ktor-docs/blob/main/src/main/kotlin/dev/h4kt/ktorDocs/dsl/RoutingDsl.kt) provided by KtorDocs

GreetingController.kt
```kotlin
import dev.h4kt.ktorDocs.dsl.get

fun Routing.configureEchoRouting() = route("/greet") {

    class EchoParams : RouteParameters() {
        val name by query.string {
            name = "name"
            description = "Name of whoever to greet"
        }
    }

    get(::EchoParams) {

        description = "An API endpoint to greet whoever hits it"

        responses {
            HttpStatusCode.OK returns typeInfo<String>()
        }

        handle {
            call.respond("Greetings, ${parameters.name}")
        }

    }

}
```

### Custom type converters
KtorDocs supports 
[all primitive types](https://kotlinlang.org/docs/types-overview.html),
`kotlin.time.Duration`, 
`kotlin.time.Instant`, 
`kotlinx.datetime.*`,
`kotlin.uuid.Uuid`,
`java.util.UUID`,
collections and `data class` types
out of the box

To handle other types you can implement a custom converter:
```kotlin
class KotlinUuidTypeConverter : TypeConverter(priority = 900) {

    override fun canConvert(type: KType): Boolean {
        return type.classifierKClass == Uuid::class
    }

    override fun convert(
        type: KType,
        parentTypes: Collection<KType>,
        classifier: KClass<*>,
        convertDownstream: (type: KType) -> OpenApiSchema
    ): OpenApiSchema {
        return OpenApiSchema.String(
            format = "UUID",
            nullable = type.isMarkedNullable
        )
    }

}

// Plugin setup
fun Application.module() {
    install(KtorDocs) {
        // ...
        typeConverters(KotlinUuidTypeConverter())
    }
}
```

### Custom authentication provider converters
KtorDocs supports [Basic](https://ktor.io/docs/server-basic-auth.html), [Bearer](https://ktor.io/docs/server-bearer-auth.html) and [OAuth](https://ktor.io/docs/server-oauth.html) providers out of the box

To handle other types of authentication providers you can implement a custom converter:
```kotlin
class BasicAuthProviderConverter : AuthProviderConverter(priority = 100) {

    override fun canConvert(
        provider: AuthenticationProvider
    ): Boolean {
        return provider is BasicAuthenticationProvider
    }

    override fun convert(
        provider: AuthenticationProvider,
        application: Application
    ): OpenApiSecurityScheme {
        require(provider is BasicAuthenticationProvider)
        return OpenApiSecurityScheme.Http(scheme = BASIC)
    }

}

// Plugin setup
fun Application.module() {
    install(KtorDocs) {
        // ...
        authProviderConverters(BasicAuthProviderConverter())
    }
}
```
