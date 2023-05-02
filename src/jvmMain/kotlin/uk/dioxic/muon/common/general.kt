package uk.dioxic.muon.common

import uk.dioxic.muon.config.Settings
import java.nio.file.Files
import kotlin.io.path.Path

fun Settings.validate() {
    require(listOf("light", "dark").contains(theme)) {
        "Theme must be 'light' or 'dark'"
    }
    require(rekordboxDatabase == null || Files.exists(Path(rekordboxDatabase))) {
        "rekordbox database [$rekordboxDatabase] does not exist!"
    }
}

fun Settings.getLocalPath(path: String) =
    this.folderMappings.fold(path) { p, (from, to) ->
        p.replace(from, to)
    }

fun ByteArray.toHex() = joinToString(separator = "") { byte -> "%02x".format(byte) }