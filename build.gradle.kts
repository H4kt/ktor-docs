plugins {

    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.plugin.serialization)

    alias(libs.plugins.dotenv)

    id("maven-publish")

}

group = "dev.h4kt"
version = "2.0.5-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.swagger)

    implementation(libs.logback)

    implementation(libs.kotlinx.datetime)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kaml)

    testImplementation(libs.ktor.server.testHost)
    testImplementation(kotlin("test"))

}

kotlin {
    jvmToolchain(11)

    compilerOptions {
//        freeCompilerArgs = listOf("-Xname-based-destructuring=complete")
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {

    repositories {

        maven {

            name = "GitHubPackageRegistry"
            url = uri("https://maven.pkg.github.com/H4kt/ktor-docs")

            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }

        }

        maven {

            name = "h4ktReleases"
            url = uri("https://repo.h4kt.dev/releases")

            authentication {
                create<BasicAuthentication>("basic")
            }

            credentials {
                username = env.H4KT_REPO_USERNAME.orNull() ?: System.getenv("H4KT_REPO_USERNAME")
                password = env.H4KT_REPO_PASSWORD.orNull() ?: System.getenv("H4KT_REPO_PASSWORD")
            }

        }

        maven {

            name = "h4ktSnapshots"
            url = uri("https://repo.h4kt.dev/snapshots")

            authentication {
                create<BasicAuthentication>("basic")
            }

            credentials {
                username = env.H4KT_REPO_USERNAME.orNull() ?: System.getenv("H4KT_REPO_USERNAME")
                password = env.H4KT_REPO_PASSWORD.orNull() ?: System.getenv("H4KT_REPO_PASSWORD")
            }

        }

    }

    publications {
        register("ktorDocs", MavenPublication::class.java) {
            from(components["java"])
        }
    }

}

tasks.test {
    useJUnitPlatform()
}
