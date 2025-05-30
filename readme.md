# ktor-docs
[![Deploy](https://github.com/H4kt/ktor-docs/actions/workflows/deploy.yml/badge.svg)](https://github.com/H4kt/ktor-docs/actions/workflows/deploy.yml)
![Kotlin](https://img.shields.io/badge/kotlin-2.1.21-purple?logo=Kotlin&label=Kotlin)

## Roadmap
- [ ] Support for custom authentications
- [ ] Support for custom type converters

## Usage

### Add dependencies
build.gradle.kts
```kotlin
repositories {
    maven("https://repo.h4kt.dev/releases")
}

dependencies {
    implementation("dev.h4kt:ktor-docs:1.6.0")
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
In order for your route to be included in the resulting specification it has to be defined via an [extension function](https://github.com/H4kt/ktor-docs/blob/main/src/main/kotlin/dev/h4kt/ktorDocs/dsl/RoutingDsl.kt) provided by the Ktor docs library.

**Make sure to import the extension function whenever attempting to use doucumentation capabilities, otherwise it won't work!**

You can define and use path and query parameters by creating a class extending [RouteParameters](https://github.com/H4kt/ktor-docs/blob/main/src/main/kotlin/dev/h4kt/ktorDocs/types/parameters/RouteParameters.kt). Those parameters will be type safe and documented in the resulting spec.

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
