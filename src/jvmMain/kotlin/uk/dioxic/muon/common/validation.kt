package uk.dioxic.muon.common

import uk.dioxic.muon.config.Settings
import uk.dioxic.muon.model.ValidationError
import uk.dioxic.muon.model.ValidationErrors
import java.nio.file.Files
import kotlin.io.path.Path

fun Settings.validate(): ValidationErrors =
    mutableListOf<ValidationError>().apply {
        if (!listOf("light", "dark").contains(theme)) {
            add("theme" to "theme not set!")
        }

        validateIsFile("rekordboxDatabase", rekordboxDatabase)
        validateIsDir("importDir", importDir)
        validateIsDir("deleteDir", deleteDir)
        validateNotEmpty("downloadDirs", downloadDirs)
        downloadDirs.forEach {
            validateIsDir("downloadDirs", it)
        }
    }

context(ValidationErrors)
fun validateNotEmpty(attrName: String, value: List<*>) =
    value.isNotEmpty().also {
        if (!it) {
            add(attrName to "$attrName is empty!")
        }
    }

context(ValidationErrors)
fun validateNotEmpty(attrName: String, value: String) =
    value.isNotEmpty().also {
        if (!it) {
            add(attrName to "$attrName is empty!")
        }
    }

context(ValidationErrors)
fun validateExists(attrName: String, value: String) =
    validateNotEmpty(attrName, value) &&
            Files.exists(Path(value)).also {
                if (!it) {
                    add(attrName to "$attrName [$value] does not exist!")
                }
            }

context(ValidationErrors)
fun validateIsDir(attrName: String, value: String) =
    validateExists(attrName, value) &&
            Files.isDirectory(Path(value)).also {
                if (!it) {
                    add(attrName to "$attrName [$value] is not a directory!")
                }
            }

context(ValidationErrors)
fun validateIsFile(attrName: String, value: String) =
    validateExists(attrName, value) &&
            Files.isRegularFile(Path(value)).also {
                if (!it) {
                    add(attrName to "$attrName [$value] is not a file!")
                }
            }