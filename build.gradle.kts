import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
}

group = "dev.leon.zimmermann.semanticsearch"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Weaviate Client
    implementation("io.weaviate:client:4.0.1")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}