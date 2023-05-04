package uk.dioxic.muon.common

import uk.dioxic.muon.config.Settings

fun Settings.getLocalPath(path: String) =
    this.folderMappings.fold(path) { p, (from, to) ->
        p.replace(from, to)
    }

fun ByteArray.toHex() = joinToString(separator = "") { byte -> "%02x".format(byte) }