import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack


plugins {
    kotlin("multiplatform") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    id("pl.allegro.tech.build.axion-release") version "1.13.14"
//    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("com.github.ben-manes.versions") version "0.42.0"
    application
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
    js {
        useCommonJs()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
            testTask {
                enabled = false
            }
//            dceTask {
//                dceOptions.devMode = true
//            }
            webpackTask {
                outputFileName = "app.js"
            }
        }
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlin.serialization.core)
                implementation(libs.koin.core)
                implementation(libs.kotlin.datetime)
                implementation(libs.kotlin.coroutines.test)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.bundles.ktorServer)
                implementation(libs.bundles.logging)
                implementation(libs.bundles.lucene)
                implementation(libs.bundles.koin)
                implementation(libs.kotlin.reflect)
                implementation(libs.clikt)
                implementation(libs.jaudiotagger)
                implementation(libs.sqlcipher)
//                implementation("io.ktor:ktor-server-sessions-jvm:2.0.0-beta-1")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.assertj)
                implementation(libs.mockk)
                implementation(libs.bundles.fixture)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(libs.bundles.ktorClient)

                implementation(
                    project.dependencies.enforcedPlatform(
                        kotlinw("wrappers-bom:1.0.0-${libs.versions.kotlinWrapper.get()}")
                    )
                )

                implementation(kotlinw("react"))
                implementation(kotlinw("react-dom"))
                implementation(kotlinw("react-query"))
                implementation(kotlinw("react-table"))
                implementation(kotlinw("react-router-dom"))

                implementation(kotlinw("emotion"))
                implementation(kotlinw("mui"))
                implementation(kotlinw("mui-icons"))

                implementation(npm("chroma-js", "2.4.2"))
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
    val taskName = if ("true" == project.findProperty("isProduction")
        || project.gradle.startParameter.taskNames.contains("installDist")) {
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


tasks.getByName<JavaExec>("run") {
    classpath(tasks.getByName<Jar>("jvmJar"))
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager")
}

//tasks.withType<ShadowJar> {
//    val webpackTask = tasks.getByName<KotlinWebpack>("jsBrowserProductionWebpack")
//    mergeServiceFiles()
////    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
//    dependsOn(webpackTask) // make sure JS gets compiled first
//    from(File(webpackTask.destinationDirectory, webpackTask.outputFileName)) {
//        into("static")
//    }
//    manifest {
//        attributes(mapOf(
//            "Main-Class" to "uk.dioxic.muon.ApplicationKt",
//            "Multi-Release" to "true"
//        ))
//    }
//}

tasks.getByName("distZip") {
    dependsOn(tasks.getByName("jsJar"))
    dependsOn(tasks.getByName("allMetadataJar"))
}

tasks.getByName("distTar") {
    dependsOn(tasks.getByName("jsJar"))
    dependsOn(tasks.getByName("allMetadataJar"))
}

//tasks.getByName("jsBrowserDevelopmentWebpack") {
//    dependsOn(tasks.getByName("jsProductionExecutableCompileSync"))
//}
//
//tasks.getByName("jsBrowserProductionWebpack") {
//    dependsOn(tasks.getByName("jsProductionExecutableCompileSync"))
//    dependsOn(tasks.getByName("jsDevelopmentExecutableCompileSync"))
//}

tasks.withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
    resolutionStrategy {
        componentSelection {
            all {
                if (isNonStable(candidate.version) && !isNonStable(currentVersion)) {
                    reject("Release candidate")
                }
            }
        }
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

