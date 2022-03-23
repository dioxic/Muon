package uk.dioxic.muon.component.page

import mui.icons.material.Delete
import mui.icons.material.Edit
import mui.icons.material.GetApp
import react.FC
import react.Props
import react.useState
import uk.dioxic.muon.component.EnhancedTable
import uk.dioxic.muon.component.RowAction
import uk.dioxic.muon.config.defaultImportTableColumns
import uk.dioxic.muon.model.ImportTableData
import uk.dioxic.muon.model.toColumns
import uk.dioxic.muon.model.toRows

val ImportPage = FC<Props> {
    val (data, setData) = useState(testData)
    val (columns, setColumns) = useState(defaultImportTableColumns)

    fun handleEditClick(id: String) {
        println("handleEdit for $id")
    }

    fun handleDeleteClick(id: String) {
        println("handleDelete for $id")
    }

    fun handleImportClick(id: String) {
        println("handleImport for $id")
    }

    val rowActions = listOf(
        RowAction(id = "edit", icon = Edit, onClick = ::handleEditClick),
        RowAction(id = "import", icon = GetApp, onClick = ::handleImportClick),
        RowAction(id = "delete", icon = Delete, onClick = ::handleDeleteClick),
    )

    EnhancedTable {
        this.title = "Music Import"
        this.rowActions = rowActions
        this.rows = data.toRows()
        this.columns = columns.toColumns()
        this.selectable = true
        this.sortable = true
    }

}

private val testData = listOf(
    ImportTableData(
        id = "1",
        title = "the one",
        artist = "mampi swift",
        bitrate = 320,
        length = 62,
        path = "c:\\library",
        filename = "sometrack.mp3",
        vbr = false
    ),
    ImportTableData(
        id = "2",
        title = "the nine",
        artist = "bad company",
        bitrate = 320,
        length = 200,
        path = "c:\\library",
        filename = "sometrack.mp3",
        vbr = false
    ),
    ImportTableData(
        id = "3",
        title = "haywire",
        artist = "dj bob",
        bitrate = 320,
        length = 185,
        path = "c:\\library",
        filename = "sometrack.mp3",
        vbr = false
    )
)