import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.5.0"
	id("org.jetbrains.intellij") version "0.7.3"
	id("org.jetbrains.changelog") version "1.1.2"
}

group = "org.nextras.orm.intellij"
version = "0.7.1"

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

intellij {
	pluginName = "Nextras Orm Plugin"
	version = "2020.1"
	updateSinceUntilBuild = false
	downloadSources = true
	alternativeIdePath = "C:\\dev\\jetbrains\\apps\\PhpStorm\\ch-0\\211.7142.44\\"
	setPlugins("com.jetbrains.php:201.6668.153")
}

tasks {
	withType<JavaCompile> {
		sourceCompatibility = "1.8"
		targetCompatibility = "1.8"
	}
	withType<KotlinCompile> {
		kotlinOptions.jvmTarget = "1.8"
	}
}
