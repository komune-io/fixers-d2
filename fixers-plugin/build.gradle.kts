plugins {
	`kotlin-dsl`
	`java-gradle-plugin`
	id("com.gradle.plugin-publish")
}

repositories {
	gradlePluginPortal()
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${PluginVersions.kotlin}")
	implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:${PluginVersions.kotlin}")

	implementation("org.jetbrains.dokka:dokka-gradle-plugin:${PluginVersions.dokka}")

	implementation(project(":dokka-storybook-plugin"))
	implementation("io.komune.fixers.gradle:config:${PluginVersions.fixers}")
	implementation("io.komune.fixers.gradle:plugin:${PluginVersions.fixers}")
}

gradlePlugin {
	website = "https://github.com/komune-io"
	vcsUrl = "https://github.com/komune-io/d2"
	plugins {
		create("io.komune.fixers.gradle.d2") {
			id = "io.komune.fixers.gradle.d2"
			implementationClass = "io.komune.d2.fixers.gradle.D2Plugin"
			displayName = "Fixers Gradle d2"
			description = "Ease the configuration of d2 in order to generate documentation for storybook."
			tags = listOf("Komune", "Fixers", "kotlin", "dokka", "d2")
		}
	}
}

apply(from = rootProject.file("gradle/publishing_plugin.gradle"))
