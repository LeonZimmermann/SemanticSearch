import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly

plugins {
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.2"
    id("com.avast.gradle.docker-compose") version "0.14.0"
    kotlin("jvm") version "1.7.20"
    kotlin("kapt") version "1.8.22"
    kotlin("plugin.spring") version "1.9.0"
}

group = "dev.leon.zimmermann.semanticsearch"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {

    implementation("org.jetbrains.kotlin.kapt:org.jetbrains.kotlin.kapt.gradle.plugin:1.8.22")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.50")

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.1.2")
    implementation("org.springframework.boot:spring-boot-starter-web:3.1.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.2")
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Weaviate Client
    implementation("io.weaviate:client:4.3.0")

    // JSoup
    implementation("org.jsoup:jsoup:1.11.3")

    // OpenNLP
    implementation("org.apache.opennlp:opennlp-tools:2.2.0")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.0")
    testImplementation("io.rest-assured:rest-assured:5.3.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
    include("**/*UnitTest.class")
}

tasks.register<Test>("integrationTest") {
    useJUnitPlatform()
    include("**/*IntegrationTest.class")
    group = "verification"
}

dockerCompose {
    useComposeFiles.add("docker-compose.test.yml")
    projectName = "${project.name.toLowerCaseAsciiOnly()}_integrationtest"
    buildBeforeUp = true
    isRequiredBy(tasks.getByName("integrationTest"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.getByName<Jar>("jar") {
    enabled = false
}
