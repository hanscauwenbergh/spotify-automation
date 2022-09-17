plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.31"
}

group = "com.hanscauwenbergh"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://dl.bintray.com/kotlin/ktor")
    maven(url = "https://kotlin.bintray.com/kotlinx")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("com.github.ajalt.clikt:clikt:3.1.0")
    implementation("com.adamratzman:spotify-api-kotlin-core:3.6.01")
    implementation("io.ktor:ktor-client-core-jvm:1.5.3")
    implementation("io.ktor:ktor-client-json:1.5.3")
    implementation("io.ktor:ktor-client-jackson:1.5.3")
    compile("io.ktor:ktor-server-netty:0.9.2")
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
