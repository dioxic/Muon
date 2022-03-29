package uk.dioxic.muon.model

import kotlinx.js.jso
import kotlin.time.Duration.Companion.seconds

private fun displayLength(length: Int) =
    length.seconds.toString()

private fun displayBoolean(bool: Boolean) =
    when (bool) {
        true -> "Yes"
        false -> "No"
    }

external interface ImportTableRow: TableRow {
    var album: String
    var artist: String
    var bitrate: String
    var comment: String
    var type: String
    var filename: String
    var genre: String
    var length: String
    var lengthSort: Int
    var lyricist: String
    var path: String
    var title: String
    var year: String
}
fun Collection<Track>.toRows() =
    this.map { it.toRow() }

fun Track.toRow(): ImportTableRow =
    jso {
        this.id = this@toRow.id
        this.album = this@toRow.album
        this.artist = this@toRow.artist
        this.bitrate = this@toRow.bitrate.toString()
        this.comment = this@toRow.comment
        this.type = this@toRow.fileType.toString()
        this.filename = this@toRow.filename
        this.genre = this@toRow.genre
        this.length = displayLength(this@toRow.length)
        this.lengthSort = this@toRow.length
        this.lyricist = this@toRow.lyricist
        this.path = this@toRow.path
        this.title = this@toRow.title
        this.year = this@toRow.year
    }