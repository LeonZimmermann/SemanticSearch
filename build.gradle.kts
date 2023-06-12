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

    implementation("com.graphql-java:graphql-java:230521-nf-execution")

    // OpenNLP
    implementation("org.apache.opennlp:opennlp-tools:2.2.0")
    implementation("org.junit.jupiter:junit-jupiter:5.8.1")
    implementation("org.junit.jupiter:junit-jupiter:5.8.1")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
