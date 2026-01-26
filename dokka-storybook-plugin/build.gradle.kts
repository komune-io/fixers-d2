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

    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.dokka.test.api)
}

tasks.withType<Jar> {
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    }
}

tasks.test {
    useJUnit()
}
