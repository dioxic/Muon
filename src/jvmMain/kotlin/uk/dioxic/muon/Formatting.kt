package uk.dioxic.muon

import uk.dioxic.muon.audio.Tags

fun formatFilename(filename: String, tags: Tags): String {

    val extension = filename.fileExtension()

    val artist = tags.artist.trim()
    val title = tags.title.originalMix().trim()

    if (artist.isNotBlank() && title.isNotBlank()) {
        return "$artist - $title.$extension"
    }

    return filename
        .spacing()
        .numericPrefix()
        .rippers()
        .ampersand()
        .doubleDash()
        .originalMix()

}

val rippers = listOf("mkd", "a0ebaebc", "oma")

private fun String.parse(): Pair<String, String>? {
    Regex("""^\s*\d*[\s-.]*([\w\s]+)[\s-]+([\w\s]+)[\s-.]*(${rippers.joinToString(separator = "|")})""")
        .matchEntire(this)
        ?.apply { return Pair(this.groupValues[0], this.groupValues[1]) }

    return null
}

private fun String.spacing() = this.replace("_", " ")

private fun String.numericPrefix() = Regex("""^\s*\d*[\s-.]*""").replace(this, "")

private fun String.rippers(): String {
    val rippers = listOf("mkd", "a0ebaebc", "oma")
    return Regex("""[\s-.]*(${rippers.joinToString(separator = "|")})""").replace(this, "")
}

private fun String.ampersand() = Regex("""\s+(and)\s+""").replace(this, " & ")

private fun String.doubleDash() = this.replace("--", "-")

private fun String.originalMix() = Regex("""[(\[]original mix[)\]]""", RegexOption.IGNORE_CASE).replace(this, "")