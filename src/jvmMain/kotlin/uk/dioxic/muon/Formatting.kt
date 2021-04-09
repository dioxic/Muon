package uk.dioxic.muon

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.Tag
import java.io.File

class MusicFileJvm(val file: File) {
    private val audioFile = AudioFileIO.read(file)

    val targetFilename: String by lazy { formatFilename(file.name, audioFile.tag) }

    val newTags: Tags by lazy {
        Tags(listOf("Bob"), "title", "comment", "genre")
    }
}

data class Tags(
    val artists: List<String>,
    val title: String,
    val comment: String,
    val genre: String
)

fun formatFilename(originalFilename: String, tag: Tag): String {

    val extension = originalFilename.extension()

    val artist = tag.artists[0].trim()
    val title = tag.title.originalMix().trim()

    if (artist.isNotBlank() && title.isNotBlank()) {
        return "$artist - $title.$extension"
    }

    return originalFilename
        .spacing()
        .numericPrefix()
        .rippers()
        .ampersand()
        .doubleDash()
        .originalMix()

}

val rippers = listOf("mkd","a0ebaebc","oma")

private fun String.parse(): Pair<String,String>? {
    Regex("""^\s*\d*[\s-.]*([\w\s]+)[\s-]+([\w\s]+)[\s-.]*(${rippers.joinToString(separator = "|")})""")
        .matchEntire(this)
        ?.apply { return Pair(this.groupValues[0], this.groupValues[1]) }

    return null
}

private fun String.extension() = Regex(""".(\w{3,4})$""").find(this)?.groupValues?.get(1)

private fun String.spacing() = this.replace("_", " ")

private fun String.numericPrefix() = Regex("""^\s*\d*[\s-.]*""").replace(this, "")

private fun String.rippers(): String {
    val rippers = listOf("mkd","a0ebaebc","oma")
    return Regex("""[\s-.]*(${rippers.joinToString(separator = "|")})""").replace(this, "")
}

private fun String.ampersand() = Regex("""\s+(and)\s+""").replace(this, " & ")

private fun String.doubleDash() = this.replace("--", "-")

private fun String.originalMix() = Regex("""[(\[]original mix[)\]]""", RegexOption.IGNORE_CASE).replace(this, "")