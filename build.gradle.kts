import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.32"
    id("pl.allegro.tech.build.axion-release") version "1.13.1"
    id("com.github.johnrengelman.shadow") version ("6.1.0")
    id("com.github.ben-manes.versions") version "0.38.0"
}

repositories {
    jcenter()
    mavenCentral()
    flatDir {
        dirs("libs")
    }
}

val spekVersion = "2.0.15"
val ktorVersion = "1.5.3"

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.github.ajalt:clikt:2.8.0")
    implementation("net.jthink:jaudiotagger:2.2.6-SNAPSHOT")

    // ktor
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-freemarker:$ktorVersion")

    // logging
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.14.1"))
    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.apache.logging.log4j:log4j-core")
    implementation("org.apache.logging.log4j:log4j-jul")

    // test
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
    testImplementation("org.assertj:assertj-core:3.19.0")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect:0.9.12")
}

scmVersion {
    repository.type = "git"
    repository.directory = rootProject.file("./")
    repository.remote = "origin"

    tag.prefix = "v"
    tag.versionSeparator = ""
}

group = "uk.dioxic.muon"
version = scmVersion.version

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines("spek2")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<ShadowJar> {
    archiveBaseName.set("shadow")
    mergeServiceFiles()
    archiveFileName.set("muon")
    manifest {
        attributes(mapOf("Main-Class" to "uk.dioxic.muon.CliKt"))
    }
}