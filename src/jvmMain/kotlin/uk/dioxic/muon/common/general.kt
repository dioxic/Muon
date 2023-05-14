package uk.dioxic.muon.common

import kotlinx.datetime.*
import uk.dioxic.muon.config.Settings

fun Settings.getLocalPath(path: String) =
    this.dirMappings.fold(path) { p, (from, to) ->
        p.replace(from, to)
    }

fun ByteArray.toHex() = joinToString(separator = "") { byte -> "%02x".format(byte) }

fun Long.toLocalDateTimeUtc() =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC)

fun LocalDateTime.toEpochSecondsUtc(): Long =
    this.toInstant(TimeZone.UTC).epochSeconds