plugins {
    kotlin("jvm")
    kotlin("plugin.jpa")
}

dependencies {
    Dependencies.Jvm.Kotlin.coroutines(::implementation)
    implementation("io.komune.f2:f2-dsl-cqrs:${PluginVersions.fixersOlderVersion}")
    implementation("io.komune.f2:f2-dsl-function:${PluginVersions.fixersOlderVersion}")
    implementation("io.komune.f2:f2-spring-boot-starter-function-http:${PluginVersions.fixersOlderVersion}")
}
