plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
}

group = "com.hanscauwenbergh"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("com.github.ajalt.clikt:clikt:3.1.0")
    implementation("com.adamratzman:spotify-api-kotlin-core:4.1.3")
    implementation("io.ktor:ktor-client-core:2.0.3")
    implementation("io.ktor:ktor-client-cio:2.0.3")
    implementation("io.ktor:ktor-client-content-negotiation:2.0.3")
    implementation("io.ktor:ktor-serialization-jackson:2.0.3")
    implementation("ch.qos.logback:logback-classic:1.2.7")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    testImplementation("org.assertj:assertj-core:3.12.2")
    testImplementation("io.mockk:mockk:1.9.3.kotlin12")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jar {

    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    manifest {
        attributes["Main-Class"] = "com.hanscauwenbergh.ApplicationKt"
    }
    // This line of code recursively collects and copies all of a project's files
    // and adds them to the JAR itself. One can extend this task, to skip certain
    // files or particular types at will
    from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}
