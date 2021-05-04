package uk.dioxic.muon

fun formatFilename(originalFilename: String, tags: Tags): String {

    val extension = originalFilename.extension()

    val artist = tags.artist.trim()
    val title = tags.title.originalMix().trim()

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

fun AudioFile.format() = AudioFileImport(
    id = this.location.path,
    originalTags = this.tags,
    originalLocation = this.location,
    standardizedTags = this.tags,
    standardizedLocation = Location(
        path = this.location.path,
        filename = formatFilename(this.location.filename, this.tags)
    ),
    header = this.header
)

val rippers = listOf("mkd", "a0ebaebc", "oma")

private fun String.parse(): Pair<String, String>? {
    Regex("""^\s*\d*[\s-.]*([\w\s]+)[\s-]+([\w\s]+)[\s-.]*(${rippers.joinToString(separator = "|")})""")
        .matchEntire(this)
        ?.apply { return Pair(this.groupValues[0], this.groupValues[1]) }

    return null
}

private fun String.extension() = Regex(""".(\w{3,4})$""").find(this)?.groupValues?.get(1)

private fun String.spacing() = this.replace("_", " ")

private fun String.numericPrefix() = Regex("""^\s*\d*[\s-.]*""").replace(this, "")

private fun String.rippers(): String {
    val rippers = listOf("mkd", "a0ebaebc", "oma")
    return Regex("""[\s-.]*(${rippers.joinToString(separator = "|")})""").replace(this, "")
}

private fun String.ampersand() = Regex("""\s+(and)\s+""").replace(this, " & ")

private fun String.doubleDash() = this.replace("--", "-")

private fun String.originalMix() = Regex("""[(\[]original mix[)\]]""", RegexOption.IGNORE_CASE).replace(this, "")