plugins {
    `kotlin-dsl`
}

repositories {
    if(System.getenv("FIXERS_REPOSITORIES_MAVEN_LOCAL") == "true" || System.getenv("MAVEN_LOCAL_USE") == "true") {
        mavenLocal()
    }
    mavenCentral()
    gradlePluginPortal()
}

val fixersVersion = "0.32.0-SNAPSHOT"

dependencies {
    implementation(kotlin("gradle-plugin", embeddedKotlinVersion))

    implementation("io.komune.fixers.gradle:config:$fixersVersion")
    implementation("io.komune.fixers.gradle:plugin:$fixersVersion")
    implementation("io.komune.fixers.gradle:dependencies:$fixersVersion")
}
