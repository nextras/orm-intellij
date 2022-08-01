import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.ideaDir
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    id("org.jetbrains.intellij") version "1.7.0"
    id("org.jetbrains.changelog") version "1.3.1"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))
    downloadSources.set(properties("platformDownloadSources").toBoolean())
    plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
    updateSinceUntilBuild.set(false)
}

changelog {
    version.set(properties("pluginVersion"))
    path.set("${project.projectDir}/changelog.md")
    groups.set(listOf("Changed"))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    patchPluginXml {
        version.set(properties("pluginVersion"))

        pluginDescription.set(
            File(projectDir, "readme.md").readText().lines().run {
                val start = "<!-- Plugin description -->"
                val end = "<!-- Plugin description end -->"

                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end))
            }.joinToString("\n").run { markdownToHTML(this) }
        )

        changeNotes.set(provider { changelog.getLatest().toHTML() })
    }

    runPluginVerifier {
        ideVersions.set(
            properties("pluginVerifierIdeVersions").split(',').map(String::trim).filter(String::isNotEmpty)
        )
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token.set(System.getenv("PUBLISH_TOKEN"))
        // pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels.set(listOf(properties("pluginVersion").split('-').getOrElse(1) { "default" }.split('.').first()))
    }
}
