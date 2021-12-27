//import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val spekVersion = "2.0.16"
val ktorVersion = "1.6.5"
val kotlinVersion = "1.6.10"
val serializationVersion = "1.3.1"
val log4jVersion = "2.16.0"
val muirwikComponentVersion = "0.9.1"
val reactVersion = "17.0.2"
val styledVersion = "5.3.0"
val kotlinJsVersion = "pre.236-kotlin-1.5.30"
val koinVersion = "3.1.4"
val luceneVersion = "9.0.0"
val assertjVersion = "3.21.0"
val mockkVersion = "1.12.1"

plugins {
    kotlin("multiplatform") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    application
    id("pl.allegro.tech.build.axion-release") version "1.13.6"
//    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("com.github.ben-manes.versions") version "0.39.0"
}

group = "uk.dioxic.muon"
version = scmVersion.version

repositories {
    mavenCentral()
}

application {
    mainClass.set("uk.dioxic.muon.ApplicationKt")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
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
                implementation(kotlin("test"))
//                implementation(kotlin("test-common"))
//                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting {
//            dependsOn(commonMain)
            dependencies {
                implementation("io.insert-koin:koin-ktor:$koinVersion")
                implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")

                implementation("io.ktor:ktor-serialization:$ktorVersion")
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-websockets:$ktorVersion")
                implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

                // logging
//                implementation(platform("org.apache.logging.log4j:log4j-bom:2.14.1"))
                implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
                implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
                implementation("org.apache.logging.log4j:log4j-jul:$log4jVersion")
                implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")

                implementation("com.github.ajalt:clikt:2.8.0")
                implementation("net.jthink:jaudiotagger:3.0.1")

                // lucene
                implementation("org.apache.lucene:lucene-core:$luceneVersion")
                implementation("org.apache.lucene:lucene-queryparser:$luceneVersion")

            }
        }

        val jvmTest by getting {
//            dependsOn(commonTest)
            dependencies {
                implementation(kotlin("test"))
//                implementation(kotlin("test-common"))
//                implementation(kotlin("test-annotations-common"))
//                implementation(platform("org.junit:junit-bom:5.8.2"))
//                implementation("org.junit.jupiter:junit-jupiter")
//                implementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
                implementation("org.assertj:assertj-core:$assertjVersion")
                implementation("io.mockk:mockk:$mockkVersion")
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
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:$reactVersion-$kotlinJsVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:$reactVersion-$kotlinJsVersion")

                //styled components
                implementation(npm("styled-components", "~$styledVersion"))
                implementation("org.jetbrains.kotlin-wrappers:kotlin-styled:$styledVersion-$kotlinJsVersion")

                // material ui
                implementation("com.ccfraser.muirwik:muirwik-components:$muirwikComponentVersion")
            }
        }
        val jsTest by getting
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

//tasks.withType<KotlinCompile> {
//    kotlinOptions.jvmTarget = "17"
//}

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

tasks.named<Copy>("jvmProcessResources") {
    val jsBrowserDistribution = tasks.named("jsBrowserDistribution")
    from(jsBrowserDistribution)
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<Jar>("jvmJar"))
    classpath(tasks.named<Jar>("jvmJar"))
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", System.getProperty("java.util.logging.manager"))
}

//tasks.withType<ShadowJar> {
//    archiveBaseName.set("shadow")
//    mergeServiceFiles()
//    archiveFileName.set("muon")
//    manifest {
//        attributes(mapOf("Main-Class" to "uk.dioxic.muon.ApplicationKt"))
//    }
//}