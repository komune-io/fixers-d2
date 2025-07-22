plugins {
	`kotlin-dsl`
	`java-gradle-plugin`
	id("com.gradle.plugin-publish")
	id("io.komune.fixers.gradle.publishing")
}

repositories {
	gradlePluginPortal()
	mavenCentral()
}

dependencies {
	implementation(libs.kotlin.gradle.plugin)
	implementation(libs.kotlin.compiler.embeddable)
	implementation(libs.dokka.gradle.plugin)

	implementation(project(":dokka-storybook-plugin"))
	implementation(libs.fixers.gradle.config)
	implementation(libs.fixers.gradle.plugin)
}

tasks.withType<Jar> {
	manifest {
		attributes(
			"Implementation-Title" to project.name,
			"Implementation-Version" to project.version
		)
	}
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

fixers {
	publish {
		gradlePlugin.set(listOf(
			"io.komune.fixers.gradle.d2PluginMarkerMaven",
		))
	}
}