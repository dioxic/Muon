package uk.dioxic.muon

import kotlinx.serialization.Serializable
import uk.dioxic.muon.ImportTarget.Original
import uk.dioxic.muon.ImportTarget.Standardized

enum class ImportTarget { Original, Standardized }

@Serializable
data class AudioFileImport(
    val original: AudioFile,
    val standardized: AudioFile
) {

    fun get(target: ImportTarget, field: AudioFileField): String =
        when (target) {
            Original -> original.get(field)
            Standardized -> standardized.get(field)
        }

    companion object {
        const val path = "/import"
        fun comparator(a: AudioFileImport, b: AudioFileImport, target: ImportTarget, orderBy: AudioFileField) =
            when (target) {
                Original -> AudioFile.comparator(a.original, b.original, orderBy)
                Standardized -> AudioFile.comparator(a.standardized, b.standardized, orderBy)
            }
    }
}
