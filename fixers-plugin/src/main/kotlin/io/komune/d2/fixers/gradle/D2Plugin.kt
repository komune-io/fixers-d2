package io.komune.d2.fixers.gradle

import getD2
import io.komune.fixers.gradle.plugin.config.ConfigPlugin
import io.komune.fixers.gradle.config.fixers
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.register

class D2Plugin : Plugin<Project> {

	companion object {
		const val DOKKA_STORYBOOK = "dokkaStorybook"
		const val DOKKA_STORYBOOK_PARTIAL = "${DOKKA_STORYBOOK}Partial"
	}

	override fun apply(target: Project) {
		target.plugins.apply("org.jetbrains.dokka")
		target.plugins.apply(ConfigPlugin::class.java)
		target.subprojects {
			tasks {
				register<org.jetbrains.dokka.gradle.DokkaTask>(DOKKA_STORYBOOK_PARTIAL) {
					dependencies {
						val currentVersion = D2Plugin::class.java.getPackage().implementationVersion
						plugins("io.komune.d2:dokka-storybook-plugin:${currentVersion}")
					}
					outputDirectory.set(file("build/d2"))
				}
			}
		}
		target.afterEvaluate {
			target.extensions.fixers?.let { config ->
				target.tasks {
					register<org.jetbrains.dokka.gradle.DokkaCollectorTask>(DOKKA_STORYBOOK) {
						addChildTask(DOKKA_STORYBOOK_PARTIAL)
						addSubprojectChildTasks(DOKKA_STORYBOOK_PARTIAL)
						outputDirectory.set(config.getD2().outputDirectory)
					}
				}
			}
		}
	}
}
