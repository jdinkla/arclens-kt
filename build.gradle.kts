plugins {
    val kotlinVersion = libs.versions.kotlinVersion.get()
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
    jacoco
    application
}

group = "net.dinkla"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization)
    implementation(libs.clikt)
    implementation(libs.kotlin.compiler.embeddable)
    implementation(libs.kotlin.logging.jvm)
    implementation(libs.slf4j.api)
    implementation(libs.logback.classic)

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.archunit)
    testImplementation(libs.archunit.junit5)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

application {
    mainClass.set("net.dinkla.arclens.ArclensKt")
}
