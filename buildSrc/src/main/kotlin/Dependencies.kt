import io.komune.fixers.gradle.dependencies.FixersDependencies
import io.komune.fixers.gradle.dependencies.FixersPluginVersions
import io.komune.fixers.gradle.dependencies.FixersVersions
import io.komune.fixers.gradle.dependencies.Scope
import java.net.URI
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.embeddedKotlinVersion

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
	maven { url = URI("https://central.sonatype.com/repository/maven-snapshots") }
	maven { url = URI("https://repo.spring.io/milestone") }
}
