plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
}

group = "net.dinkla"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":"))
    implementation(libs.kotlinx.serialization)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlin.compiler.embeddable)
}

gradlePlugin {
    plugins {
        create("arclens") {
            id = "net.dinkla.arclens"
            implementationClass = "net.dinkla.arclens.gradle.ArclensPlugin"
            displayName = "Arclens Kotlin Analysis Plugin"
            description = "Static analysis for Kotlin programs - package dependencies, class hierarchies, and metrics"
        }
    }
}

kotlin {
    jvmToolchain(21)
}
