plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.allopen") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    id("io.quarkus")
    id("com.diffplug.spotless") version "7.0.2"
}

spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint("0.50.0")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    gradlePluginPortal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
//    implementation("io.quarkus:quarkus-amazon-lambda")
    implementation("io.quarkus:quarkus-amazon-lambda-http")
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-rest-client-jsonb")
    implementation("io.quarkus:quarkus-rest-client-kotlin-serialization")
    implementation("io.quarkus:quarkus-rest-client")
    implementation("io.quarkus:quarkus-rest-kotlin-serialization")
    implementation("io.quarkus:quarkus-rest-client-jackson")
    implementation("io.quarkus:quarkus-amazon-lambda-xray")
    implementation("io.quarkus:quarkus-rest-jsonb")
    implementation("io.quarkus:quarkus-smallrye-openapi")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-rest-client-jaxb")
    implementation("io.quarkus:quarkus-rest-jaxb")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.diffplug.gradle.spotless:com.diffplug.gradle.spotless.gradle.plugin:7.0.2")
    implementation("io.quarkus:quarkus-arc")
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.assertj:assertj-core:3.6.1")
    testImplementation("io.mockk:mockk:1.13.17")
}

group = "kt"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}
allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("jakarta.persistence.Entity")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
        javaParameters = true
    }
}
