plugins {
    `kotlin-dsl`
}

repositories {
    if(System.getenv("MAVEN_LOCAL_USE") == "true") {
        mavenLocal()
    }
    mavenCentral()
    gradlePluginPortal()
}

val fixersVersion = file("../VERSION").readText().trim()

dependencies {
    implementation(kotlin("gradle-plugin", embeddedKotlinVersion))

    implementation(libs.detektGradlePlugin)
    implementation(libs.jreleaserGradlePlugin)
    implementation(libs.npmPublishGradlePlugin)
    implementation(libs.sonarqubeGradlePlugin)

    implementation("io.komune.fixers.gradle:config:$fixersVersion")
    implementation("io.komune.fixers.gradle:plugin:$fixersVersion")
    implementation("io.komune.fixers.gradle:dependencies:$fixersVersion")
}
