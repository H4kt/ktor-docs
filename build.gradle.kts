plugins {

    kotlin("jvm") version "1.9.21"
    kotlin("plugin.serialization") version "1.9.21"

    id("maven-publish")
    id("co.uzzu.dotenv.gradle") version "2.0.0"

}

group = "dev.h4kt"
version = "1.4.0"

val ktorVersion: String by project
val gitlabProjectId: String by project

repositories {
    mavenCentral()
}

dependencies {

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-swagger:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:1.4.11")

    api("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")

    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    api("com.charleskorn.kaml:kaml:0.55.0")

    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("io.ktor:ktor-client-cio:$ktorVersion")

    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.10")

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
