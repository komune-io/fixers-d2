
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://central.sonatype.com/repository/maven-snapshots") }
    }
}

rootProject.name = "fixers-d2"

includeBuild("build-composite")

include("dokka-storybook-plugin")
include("fixers-plugin")
include("sample")
include("sample-fragment")
