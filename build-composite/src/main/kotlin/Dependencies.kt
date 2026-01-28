import io.komune.fixers.gradle.dependencies.FixersDependencies
import io.komune.fixers.gradle.dependencies.FixersPluginVersions
import io.komune.fixers.gradle.dependencies.Scope
import java.net.URI
import org.gradle.api.artifacts.dsl.RepositoryHandler

object PluginVersions {
    val fixers = FixersPluginVersions.fixers
    val fixersOlderVersion = "0.26.0"
    var kotlin = FixersPluginVersions.kotlin
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
    if(System.getenv("MAVEN_LOCAL_USE") == "true") {
        mavenLocal()
    }
}
