import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.5.31"
}

group = "com.volcano"
version = "1.0"

val lwjglVersion = "3.2.3"
val lwjglNatives = "natives-windows"

repositories {
	mavenCentral()
	maven("https://jitpack.io")
}

dependencies {
	fun ktx(dep: String): String {
		return "io.github.libktx:ktx-$dep:1.10.0-b3"
	}

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
	implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.31")
	implementation("com.formdev:flatlaf:1.6")

	implementation(ktx("actors"))
	implementation(ktx("preferences"))
	implementation(ktx("async"))
	implementation(ktx("log"))
	implementation(ktx("vis"))
	implementation(ktx("math"))
	implementation(ktx("app"))
	implementation(ktx("graphics"))

	implementation("com.badlogicgames.gdx:gdx-backend-lwjgl:1.10.0")
	implementation("com.badlogicgames.gdx:gdx-platform:1.10.0:natives-desktop")
	implementation("com.badlogicgames.gdx:gdx:1.10.0")
}



tasks.withType<KotlinCompile>() {
	kotlinOptions.jvmTarget = "1.8"
}