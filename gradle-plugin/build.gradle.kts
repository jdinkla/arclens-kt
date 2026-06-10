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

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(gradleTestKit())
}

// Dedicated source set for TestKit functional tests so the plugin (and its
// project(":") dependency) is available on the test classpath via withPluginClasspath().
val functionalTest: SourceSet by sourceSets.creating

configurations[functionalTest.implementationConfigurationName]
    .extendsFrom(configurations.testImplementation.get())
configurations[functionalTest.runtimeOnlyConfigurationName]
    .extendsFrom(configurations.testRuntimeOnly.get())

val functionalTestTask =
    tasks.register<Test>("functionalTest") {
        description = "Runs the Gradle TestKit functional tests."
        group = "verification"
        testClassesDirs = functionalTest.output.classesDirs
        classpath = functionalTest.runtimeClasspath
        useJUnitPlatform()
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
    // Make the plugin-under-test metadata available to the functional test source set
    // so TestKit's withPluginClasspath() can resolve the plugin and its dependencies.
    testSourceSets(functionalTest)
}

tasks.test {
    useJUnitPlatform()
}

tasks.check {
    dependsOn(functionalTestTask)
}

kotlin {
    jvmToolchain(21)
}
