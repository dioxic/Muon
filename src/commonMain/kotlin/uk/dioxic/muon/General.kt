package uk.dioxic.muon

import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.audio.ImportError

fun Int.toTimeString(): String {
    val minutes = this.floorDiv(60)
    val seconds = this.mod(60).toString().padStart(2, '0')
    return "${minutes}m ${seconds}s"
}

fun <t> List<t>.swap(a: Int, b: Int): List<t> = this
    .toMutableList()
    .also {
        it[a] = this[b]
        it[b] = this[a]
    }

fun <T> coalesce(ignore: String, vararg items: T): T {
    items.forEach {
        if (it != null) {
            if (ignore == it) {
                // goto next item
            } else {
                return it
            }
        }
    }
    return items.first()
}

fun String.fileExtension() =
    Regex(""".(\w{3,4})$""")
        .find(this)
        ?.groupValues
        ?.get(1)

fun <T> coalesce(vararg items: T) = coalesce("", items)

fun List<AudioFile>.merge(other: List<AudioFile>): List<AudioFile> =
    this.map { a ->
        other.findLast { it.id == a.id } ?: a
    }

fun List<AudioFile>.containsId(id: String): Boolean =
    this.count { it.id == id } > 0

fun List<AudioFile>.filterFailures(failures: List<ImportError>) =
    this.filterIds(failures.map { it.id })

fun List<AudioFile>.filterIds(ids: List<String>) =
    this.filterNot { ids.contains(it.id) }

fun List<AudioFile>.filterFiles(files: List<AudioFile>) =
    this.filterIds(files.map { it.id })

fun String.removeProblemCharacters() =
    this
        .replace("[", "")
        .replace("]", "")
        .replace(".", "")
        .replace("_", "")
        .replace("/", "")
        .replace("\\", "")

fun String?.nullIfBlank() =
    if (this.isNullOrBlank()) null else this

//fun <t> MutableList<t>.swap(a: Int, b: Int): List<t> = this
//    .also {
//        it[a] = this[b]
//        it[b] = this[a]
//    }