import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val spekVersion = "2.0.15"
val ktorVersion = "1.5.3"
val kotlinVersion = "1.5.0-M2"
val serializationVersion = "1.1.0"
val log4jVersion = "2.14.1"

plugins {
    kotlin("multiplatform") version "1.5.0-M2"
    kotlin("plugin.serialization") version "1.5.0-M2"
    application
    id("pl.allegro.tech.build.axion-release") version "1.13.1"
    id("com.github.johnrengelman.shadow") version ("6.1.0")
    id("com.github.ben-manes.versions") version "0.38.0"
}

group = "uk.dioxic.muon"
version = scmVersion.version

repositories {
    jcenter()
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
    flatDir {
        dirs("libs")
    }
}

application {
    mainClass.set("uk.dioxic.muon.ApplicationKt")
}

kotlin {
    jvm {
        withJava()
    }
    js {
        browser {
            binaries.executable()
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-serialization:$ktorVersion")
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-websockets:$ktorVersion")
                implementation("org.litote.kmongo:kmongo-coroutine-serialization:4.2.5")
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.0-M2")
                // logging
//                implementation(platform("org.apache.logging.log4j:log4j-bom:2.14.1"))
                implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
                implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
                implementation("org.apache.logging.log4j:log4j-jul:$log4jVersion")
                implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")

                implementation("com.github.ajalt:clikt:2.8.0")
                implementation("net.jthink:jaudiotagger:2.2.6-SNAPSHOT")

            }
        }

        val jvmTest by getting {
            dependencies {
                implementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
                implementation("org.assertj:assertj-core:3.19.0")
                runtimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js:$ktorVersion") //include http&websockets

                //ktor client js json
                implementation("io.ktor:ktor-client-json-js:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization-js:$ktorVersion")

                //React, React DOM + Wrappers
                implementation("org.jetbrains:kotlin-react:17.0.1-pre.148-kotlin-1.4.30")
                implementation("org.jetbrains:kotlin-react-dom:17.0.1-pre.148-kotlin-1.4.30")
                implementation(npm("react", "17.0.1"))
                implementation(npm("react-dom", "17.0.1"))

                //styled components
                implementation("org.jetbrains:kotlin-styled:5.2.1-pre.148-kotlin-1.4.21")
                implementation(npm("styled-components", "~5.2.1"))

                // material ui
                
            }
        }
    }
}

scmVersion {
    repository.type = "git"
    repository.directory = rootProject.file("./")
    repository.remote = "origin"

    tag.prefix = "v"
    tag.versionSeparator = ""
}

// include JS artifacts in any JAR we generate
tasks.getByName<Jar>("jvmJar") {
    val taskName = if ("true" == project.findProperty("isProduction")) {
        "jsBrowserProductionWebpack"
    } else {
        "jsBrowserDevelopmentWebpack"
    }
    val webpackTask = tasks.getByName<KotlinWebpack>(taskName)
    dependsOn(webpackTask) // make sure JS gets compiled first
    from(File(webpackTask.destinationDirectory, webpackTask.outputFileName)) // bring output file along into the JAR
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines("spek2")
    }
}

distributions {
    main {
        contents {
            from("$buildDir/libs") {
                rename("${rootProject.name}-jvm", rootProject.name)
                into("lib")
            }
        }
    }
}

// Alias "installDist" as "stage" (for cloud providers)
tasks.create("stage") {
    dependsOn(tasks.getByName("installDist"))
}

tasks.getByName<JavaExec>("run") {
    classpath(tasks.getByName<Jar>("jvmJar")) // so that the JS artifacts generated by `jvmJar` can be found and served
}

tasks.withType<ShadowJar> {
    archiveBaseName.set("shadow")
    mergeServiceFiles()
    archiveFileName.set("muon")
    manifest {
        attributes(mapOf("Main-Class" to "uk.dioxic.muon.CliKt"))
    }
}