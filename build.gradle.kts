plugins {
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"
}

group = "dev.h4kt"
version = "1.1.0"

val ktorVersion: String by project

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

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}
