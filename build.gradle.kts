plugins {
    alias(libs.plugins.kotlin.jpa) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false

    alias(libs.plugins.dokka)
    alias(libs.plugins.gradlePublish) apply false
    id("io.komune.fixers.gradle.config")
    id("io.komune.fixers.gradle.check")
}

allprojects {
    group = "io.komune.d2"
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

fixers {
    bundle {
        id = "d2"
        name = "Gradle D2"
        description = "Gradle D2 documentation generator for Komune projects"
        url = "https://github.com/komune-io/fixers-gradle"
    }
    sonar {
        organization = "komune-io"
        projectKey = "komune-io_fixers-d2"
    }
}
