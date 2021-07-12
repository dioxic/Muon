//import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val spekVersion = "2.0.16"
val ktorVersion = "1.6.1"
val kotlinVersion = "1.5.20"
val serializationVersion = "1.2.2"
val log4jVersion = "2.14.1"
//val muirwikComponentVersion = "0.6.7-IR"
val muirwikComponentVersion = "0.8.2"
val reactVersion = "17.0.2"
val kotlinJsWrapperVersion = "pre.213-kotlin-1.5.20"

plugins {
    kotlin("multiplatform") version "1.5.20"
    kotlin("plugin.serialization") version "1.5.20"
    application
    id("pl.allegro.tech.build.axion-release") version "1.13.3"
//    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("com.github.ben-manes.versions") version "0.39.0"
}

group = "uk.dioxic.muon"
version = scmVersion.version

repositories {
    mavenCentral()
    mavenLocal()
    flatDir {
        dirs("libs")
    }
}

application {
//    mainClass.set("uk.dioxic.muon.ApplicationKt")
    @Suppress("DEPRECATION")
    mainClassName = "uk.dioxic.muon.ApplicationKt"
}

kotlin {
    jvm {
        withJava()
    }
    js(IR) {
        useCommonJs()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
//            dceTask {
//                dceOptions.devMode = true
//            }
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
//            dependsOn(commonMain)
            dependencies {
                implementation("io.ktor:ktor-serialization:$ktorVersion")
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-websockets:$ktorVersion")
//                implementation("org.litote.kmongo:kmongo-coroutine-serialization:4.2.5")
                implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
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
//            dependsOn(commonTest)
            dependencies {
//                implementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
                implementation("org.assertj:assertj-core:3.20.2")
//                runtimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
//                runtimeOnly("org.spekframework.spek2:spek-runtime-jvm:$spekVersion")
            }
        }

        val jsMain by getting {
//            dependsOn(commonMain)
            dependencies {
                implementation(kotlin("stdlib-js", kotlinVersion))

                //ktor client js json
                implementation("io.ktor:ktor-client-js:$ktorVersion") //include http&websockets
                implementation("io.ktor:ktor-client-json-js:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization-js:$ktorVersion")

                //React, React DOM + Wrappers
                implementation(npm("react", reactVersion))
                implementation(npm("react-dom", reactVersion))
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:$reactVersion-$kotlinJsWrapperVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:$reactVersion-$kotlinJsWrapperVersion")

                //styled components
                implementation(npm("styled-components", "~5.3.0"))
                implementation("org.jetbrains.kotlin-wrappers:kotlin-styled:5.3.0-$kotlinJsWrapperVersion")

                // material ui
                implementation("com.ccfraser.muirwik:muirwik-components:$muirwikComponentVersion")
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
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn(webpackTask) // make sure JS gets compiled first
    from(File(webpackTask.destinationDirectory, webpackTask.outputFileName)) // bring output file along into the JAR
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

//tasks.withType<Test> {
//    useJUnitPlatform {
//        includeEngines("spek2")
//    }
//}

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

//tasks.withType<ShadowJar> {
//    archiveBaseName.set("shadow")
//    mergeServiceFiles()
//    archiveFileName.set("muon")
//    manifest {
//        attributes(mapOf("Main-Class" to "uk.dioxic.muon.ApplicationKt"))
//    }
//}