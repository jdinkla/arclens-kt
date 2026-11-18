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

// ktlint's CLI (even 1.8.0) still depends on logback 1.3.x, which has open CVEs.
// Force the patched logback line on ktlint's own resolution configurations.
configurations
    .matching { it.name.startsWith("ktlint") }
    .configureEach {
        resolutionStrategy.eachDependency {
            if (requested.group == "ch.qos.logback") {
                useVersion(
                    libs.versions.logback.classic
                        .get(),
                )
                because("logback < 1.5.34 is affected by GHSA-p47f-322f-whfh and GHSA-jhq6-gfmj-v8fx")
            }
        }
    }
