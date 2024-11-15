plugins {
    kotlin("plugin.jpa") version PluginVersions.kotlinDsl apply false
    kotlin("kapt") version PluginVersions.kotlinDsl apply false
    kotlin("jvm") version PluginVersions.kotlinDsl apply false
    kotlin("multiplatform") version PluginVersions.kotlinDsl apply false

    id("org.jetbrains.dokka") version PluginVersions.dokka
    id("com.gradle.plugin-publish") version PluginVersions.gradlePublish apply false
}

allprojects {
    group = "io.komune.d2"
    version = System.getenv("VERSION") ?: "local"
    repositories {
        defaultRepo()
    }
}

val dokkaStorybook = "dokkaStorybook"
val dokkaStorybookPartial = "${dokkaStorybook}Partial"

subprojects {
    tasks {
        register<org.jetbrains.dokka.gradle.DokkaTask>(dokkaStorybookPartial) {
            dependencies {
                plugins(project(":dokka-storybook-plugin"))
            }
            outputDirectory.set(file("build/d2"))
        }
    }
}

tasks {
    register<org.jetbrains.dokka.gradle.DokkaCollectorTask>(dokkaStorybook) {
        dependencies {
            plugins(project(":dokka-storybook-plugin"))
        }
        addChildTask(dokkaStorybookPartial)
        addSubprojectChildTasks(dokkaStorybookPartial)
        outputDirectory.set(file("storybook/stories/d2"))
    }
}
