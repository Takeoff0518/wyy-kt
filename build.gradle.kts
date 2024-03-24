plugins {
    kotlin("jvm") version "1.9.20"
    application
    kotlin("plugin.serialization") version "1.9.20"
}

group = "top.tbpdt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion = "2.3.8"
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-json:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}