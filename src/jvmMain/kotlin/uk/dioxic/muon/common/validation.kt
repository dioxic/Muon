package uk.dioxic.muon.common

import uk.dioxic.muon.config.Settings
import uk.dioxic.muon.model.ValidationError
import uk.dioxic.muon.model.ValidationErrors
import java.nio.file.Files
import kotlin.io.path.Path

fun Settings.validate(): ValidationErrors =
    mutableListOf<ValidationError>().apply {
        if (!listOf("light", "dark").contains(theme)) {
            add(ValidationError("theme", "theme not set!"))
        }

        validateIsFile("rekordboxDatabase", rekordboxDatabase)
        validateIsDir("importDir", importDir)
        validateIsDir("deleteDir", deleteDir)
        validateNotEmpty("downloadDirs", downloadDirs)
        downloadDirs.forEach {
            validateIsDir("downloadDirs", it)
        }
        dirMappings.forEach {
            validateNotEmpty("rbDir", it.rbDir)
            validateIsDir("hostDir", it.hostDir)
        }
    }

context(ValidationErrors)
fun addError(valid: Boolean, attrName: String, errMsg: String): Boolean {
    if (!valid) {
        add(ValidationError(attrName, errMsg))
    }
    return valid
}

context(ValidationErrors)
fun validateNotEmpty(attrName: String, value: List<*>) =
    addError(value.isNotEmpty(), attrName, "$attrName is empty!")

context(ValidationErrors)
fun validateNotEmpty(attrName: String, value: String) =
    addError(value.isNotEmpty(), attrName, "$attrName is empty!")

context(ValidationErrors)
fun validateExists(attrName: String, value: String) =
    validateNotEmpty(attrName, value) &&
            addError(Files.exists(Path(value)), attrName, "$attrName [$value] does not exist!")

context(ValidationErrors)
fun validateIsDir(attrName: String, value: String) =
    validateExists(attrName, value) &&
            addError(Files.isDirectory(Path(value)), attrName, "$attrName [$value] is not a directory!")

context(ValidationErrors)
fun validateIsFile(attrName: String, value: String) =
    validateExists(attrName, value) &&
            addError(Files.isRegularFile(Path(value)), attrName, "$attrName [$value] is not a file!")
