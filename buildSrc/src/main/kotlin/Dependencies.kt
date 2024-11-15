import io.komune.gradle.dependencies.FixersDependencies
import io.komune.gradle.dependencies.FixersPluginVersions
import io.komune.gradle.dependencies.FixersVersions
import io.komune.gradle.dependencies.Scope
import org.gradle.kotlin.dsl.embeddedKotlinVersion
import java.net.URI
import org.gradle.api.artifacts.dsl.RepositoryHandler

object PluginVersions {
	val fixers = FixersPluginVersions.fixers
	val fixersOlderVersion = "0.17.0"
	var kotlinDsl = embeddedKotlinVersion
	var kotlin = FixersPluginVersions.kotlin
	var dokka = "1.9.20"
	const val gradlePublish = "1.2.0"
}

object Versions {
	const val junit = FixersVersions.Test.junit
	const val jackson = FixersVersions.Json.jackson
}

object Dependencies {
	object Jvm {
		object Kotlin {
			fun coroutines(scope: Scope) = FixersDependencies.Jvm.Kotlin.coroutines(scope)
		}
	}
}

fun RepositoryHandler.defaultRepo() {
	mavenCentral()
	maven { url = URI("https://s01.oss.sonatype.org/content/repositories/snapshots") }
	maven { url = URI("https://repo.spring.io/milestone") }
}
