plugins {
    `kotlin-dsl`
    id("io.komune.fixers.gradle.kotlin.jvm")
    id("io.komune.fixers.gradle.publishing")
}


project.plugins.withId("java-gradle-plugin") { // only do it if it's actually applied
    project.configure<GradlePluginDevelopmentExtension> {
        isAutomatedPublishing = false
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    compileOnly(libs.dokka.core)
    implementation(libs.dokka.base)
    implementation(libs.dokka.gfm.plugin)
    implementation(libs.kotlin.stdlib)
    implementation(libs.jackson.databind)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.dokka.test.api)
}
