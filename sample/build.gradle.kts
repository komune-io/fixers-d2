import io.komune.gradle.dependencies.FixersDependencies

plugins {
    kotlin("jvm")
    kotlin("plugin.jpa")
}

dependencies {
    FixersDependencies.Jvm.Kotlin.coroutines(::implementation)
    implementation("io.komune.f2:f2-dsl-cqrs:0.8.0")
    implementation("io.komune.f2:f2-dsl-function:0.8.0")
    implementation("io.komune.f2:f2-spring-boot-starter-function-http:0.8.0")
}
