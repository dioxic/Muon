package uk.dioxic.muon.model

import csstype.HtmlAttributes
import csstype.Length
import csstype.px
import kotlinx.js.jso
import mui.lab.CalendarPickerView
import mui.material.TableCellAlign
import uk.dioxic.muon.config.TableColumnConfig
import kotlin.time.Duration.Companion.seconds

private fun displayLength(length: Int) =
    length.seconds.toString()

private fun displayBoolean(bool: Boolean) =
    when (bool) {
        true -> "Yes"
        false -> "No"
    }

private fun String.toTableCellAlign() =
    when (this) {
        "left" -> TableCellAlign.left
        "center" -> TableCellAlign.center
        "right" -> TableCellAlign.right
        "justify" -> TableCellAlign.justify
        else -> TableCellAlign.left
    }

external interface TableColumn {
    var id: String
    var label: String
    var minWidth: Length
    var align: TableCellAlign
    var visible: Boolean
}

external interface TableRow {
    var id: String
}

external interface ImportTableRow: TableRow {
    var title: String
    var artist: String
    var lyricist: String
    var genre: String
    var comment: String
    var bitrate: String
    var vbr: String
    var type: String
    var path: String
    var filename: String
    var length: String
    var lengthSort: Int
    var year: String
}

//external interface SortAndDisplay {
//    var display: String
//    var sort: Any
//}

fun Collection<TableColumnConfig>.toColumns() =
    this.map { it.toColumn() }

fun TableColumnConfig.toColumn(): TableColumn =
    jso {
        this.id = this@toColumn.id
        this.label = this@toColumn.label
        this.minWidth = this@toColumn.minWidth.px
        this.align = this@toColumn.align.toTableCellAlign()
        this.visible = this@toColumn.visible
    }

fun Collection<ImportTableData>.toRows() =
    this.map { it.toRow() }

fun ImportTableData.toRow(): ImportTableRow =
    jso {
        this.id = this@toRow.id
        this.title = this@toRow.title
        this.artist = this@toRow.artist
        this.lyricist = this@toRow.lyricist
        this.genre = this@toRow.genre
        this.comment = this@toRow.comment
        this.bitrate = this@toRow.bitrate.toString()
        this.vbr = displayBoolean(this@toRow.vbr)
        this.type = this@toRow.type
        this.path = this@toRow.path
        this.filename = this@toRow.filename
        this.length = displayLength(this@toRow.length)
        this.lengthSort = this@toRow.length
        this.year = this@toRow.year
    }