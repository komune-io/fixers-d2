
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots") }
    }
}

rootProject.name = "fixers-d2"

include("dokka-storybook-plugin")
include("fixers-plugin")
include("sample")
include("sample-fragment")
