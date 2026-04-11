
pluginManagement {
    repositories {
        if(System.getenv("FIXERS_REPOSITORIES_MAVEN_LOCAL") == "true") {
            mavenLocal()
        }
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://central.sonatype.com/repository/maven-snapshots") }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://central.sonatype.com/repository/maven-snapshots") }
        if(System.getenv("FIXERS_REPOSITORIES_MAVEN_LOCAL") == "true") {
            mavenLocal()
        }
    }
}

rootProject.name = "fixers-d2"

includeBuild("build-composite")

include("dokka-storybook-plugin")
include("fixers-plugin")
include("sample")
include("sample-fragment")
