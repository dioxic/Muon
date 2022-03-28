//import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack


plugins {
    kotlin("multiplatform") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
//    alias(libs.plugins.kotlin.serialization)
//    alias(libs.plugins.axion)
//    alias(libs.plugins.versions)
    application
    id("pl.allegro.tech.build.axion-release") version "1.13.6"
//    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("com.github.ben-manes.versions") version "0.42.0"
//    id("com.github.turansky.kfc.webpack") version "4.61.0"
}

version = scmVersion.version

repositories {
    mavenCentral()
}

application {
    mainClass.set("uk.dioxic.muon.ApplicationKt")
}

fun kotlinw(target: String): String =
    "org.jetbrains.kotlin-wrappers:kotlin-$target"

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
        }
        binaries.executable()
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(libs.kotlin.serialization.core)
                implementation(libs.ktor.client.core)
                implementation(libs.kotlin.datetime)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.bundles.ktorServer)
                implementation(libs.bundles.logging)
                implementation(libs.bundles.lucene)
                implementation(libs.koin.ktor)
                implementation(libs.koin.logger.slf4j)
                implementation(libs.kotlin.reflect)
                implementation(libs.clikt)
                implementation(libs.jaudiotagger)
                implementation(libs.sqlcipher)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlin.coroutines.test)
                implementation(libs.assertj)
                implementation(libs.mockk)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation(libs.bundles.ktorClient)

                implementation(
                    project.dependencies.enforcedPlatform(
                        kotlinw("wrappers-bom:0.0.1-${libs.versions.kotlinWrapper.get()}")
                    )
                )

                implementation(kotlinw("react"))
                implementation(kotlinw("react-dom"))
                implementation(kotlinw("react-css"))
                implementation(kotlinw("react-router-dom"))

                implementation(kotlinw("mui"))
                implementation(kotlinw("mui-icons"))

                implementation(npm("@emotion/react", libs.versions.emotion.get()))
                implementation(npm("@emotion/styled", libs.versions.emotion.get()))
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
    from(File(webpackTask.destinationDirectory, webpackTask.outputFileName)) {
        into("static")
    }
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
//tasks.create("stage") {
//    dependsOn(tasks.getByName("installDist"))
//}

//tasks.named<Copy>("jvmProcessResources") {
//    val jsBrowserDistribution = tasks.named("jsBrowserDistribution")
//    from(jsBrowserDistribution)
//}

tasks.named<JavaExec>("run") {
    classpath(tasks.named<Jar>("jvmJar"))
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager")
}

tasks.getByName("distZip") {
    dependsOn(tasks.getByName("jsJar"))
    dependsOn(tasks.getByName("metadataJar"))
}

tasks.getByName("distTar") {
    dependsOn(tasks.getByName("jsJar"))
    dependsOn(tasks.getByName("metadataJar"))
}

//tasks.withType<ShadowJar> {
//    archiveBaseName.set("shadow")
//    mergeServiceFiles()
//    archiveFileName.set("muon")
//    manifest {
//        attributes(mapOf("Main-Class" to "uk.dioxic.muon.ApplicationKt"))
//    }
//}