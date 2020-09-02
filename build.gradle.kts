import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.0"
    id("pl.allegro.tech.build.axion-release") version "1.11.0"
    id("com.github.johnrengelman.shadow") version ("6.0.0")
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
}

scmVersion {
    repository.type = "git"
    repository.directory = rootProject.file("./")
    repository.remote = "origin"

    tag.prefix = "v"
    tag.versionSeparator = ""
}

group = "uk.dioxic.muorg"
version = scmVersion.version

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<ShadowJar> {
    archiveBaseName.set("shadow")
    mergeServiceFiles()
    archiveFileName.set("muorg")
    manifest {
        attributes(mapOf("Main-Class" to "uk.dioxic.muorg.CliKt"))
    }
}