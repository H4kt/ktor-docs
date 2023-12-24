# ktor-docs
[![deploy](https://github.com/H4kt/ktor-docs/actions/workflows/deploy.yml/badge.svg)](https://github.com/H4kt/ktor-docs/actions/workflows/deploy.yml)
![kotlin](https://img.shields.io/badge/kotlin-1.9.21-purple)

## Roadmap
- [ ] Support for custom authentications
- [ ] Support for custom type converters
- [ ] Support for poducing different versions of OpenAPI spec

## Usage

### Add dependencies
build.gradle.kts
```kotlin
repositories {
    maven("https://repo.h4kt.dev/releases")
}

dependencies {
    implementation("dev.h4kt:ktor-docs:1.3.9")
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
