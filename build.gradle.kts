plugins {

    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.plugin.serialization)

    alias(libs.plugins.dotenv)

    id("maven-publish")

}

group = "dev.h4kt"
version = "1.6.0"

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

            name = "h4kt"
            url = uri("https://repo.h4kt.dev/releases")

            authentication {
                create<BasicAuthentication>("basic")
            }

            credentials {
                username = System.getenv("H4KT_REPO_USERNAME")
                password = System.getenv("H4KT_REPO_PASSWORD")
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
