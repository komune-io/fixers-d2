import io.komune.gradle.dependencies.FixersDependencies
import io.komune.gradle.dependencies.FixersPluginVersions

plugins {
    kotlin("jvm")
    kotlin("plugin.jpa")
}

dependencies {
    FixersDependencies.Jvm.Kotlin.coroutines(::implementation)
    implementation("io.komune.f2:f2-dsl-cqrs:${FixersPluginVersions.fixers}")
    implementation("io.komune.f2:f2-dsl-function:${FixersPluginVersions.fixers}")
    implementation("io.komune.f2:f2-spring-boot-starter-function-http:${FixersPluginVersions.fixers}")
}
