package uk.dioxic.muon.component.page

import kotlinx.js.ReadonlyArray
import kotlinx.js.jso
import mui.icons.material.Delete
import mui.icons.material.Edit
import mui.icons.material.GetApp
import mui.icons.material.Refresh
import mui.material.Box
import mui.material.IconButtonColor
import mui.material.Paper
import react.FC
import react.Props
import react.table.*
import react.useMemo
import react.useState
import uk.dioxic.muon.component.dialog.TrackEditDialog
import uk.dioxic.muon.component.table.EnhancedTable
import uk.dioxic.muon.component.table.RowAction
import uk.dioxic.muon.component.table.ToolbarAction
import uk.dioxic.muon.component.table.plugin.useCheckboxSelect
import uk.dioxic.muon.component.table.plugin.useRowActions
import uk.dioxic.muon.hook.useImport
import uk.dioxic.muon.hook.useReloadImport
import uk.dioxic.muon.model.FileType
import uk.dioxic.muon.model.Track
import kotlin.time.Duration.Companion.seconds


private val COLUMNS = columns<Track> {

    column<String> {
        header = "Title"
        accessorFunction = { it.title }
    }
    column<String> {
        header = "Artist"
        accessorFunction = { it.artist }
    }
    column<String> {
        header = "Genre"
        accessorFunction = { it.genre }
    }
    column<String> {
        header = "Album"
        accessorFunction = { it.album }
    }
    column<String> {
        header = "Lyricist"
        accessorFunction = { it.lyricist }
    }
    column<String> {
        header = "Comment"
        accessorFunction = { it.comment }
    }
    column<Int> {
        header = "Bitrate"
        accessorFunction = { it.bitrate }
    }
    column<String> {
        header = "Filename"
        accessorFunction = { it.filename }
    }
    column<String> {
        header = "Year"
        accessorFunction = { it.year }
    }
    column<String> {
        header = "Length"
        accessorFunction = { it.length.seconds.toString() }
    }
    column<FileType> {
        header = "Type"
        accessorFunction = { it.type }
    }
}

val ImportPage = FC<Props> {
    val import = useImport()
    val reloadImport = useReloadImport()
    val (dialogOpen, setDialogOpen) = useState(false)
    val (currentTrack, setCurrentTrack) = useState<Track?>(null)

    fun handleEditClick(row: Row<Track>) {
        setCurrentTrack(row.original)
        setDialogOpen(true)
    }

    fun handleDeleteClick(row: Row<Track>) {
        println("handleDelete for $row")
    }

    fun handleDeleteClick(selected: ReadonlyArray<Row<*>>) {
        println("handleDelete for $selected")
    }

    fun handleImportClick(row: Row<Track>) {
        println("handleImport for $row")
    }

    fun handleImportClick(selected: ReadonlyArray<Row<*>>) {
        println("handleImport for $selected")
    }

    @Suppress("UNUSED_PARAMETER")
    fun handleRefreshClick(selected: ReadonlyArray<Row<*>>) {
        reloadImport()
    }

    val rowActions = listOf(
        RowAction(name = "edit", icon = Edit, onClick = ::handleEditClick),
        RowAction(name = "import", icon = GetApp, onClick = ::handleImportClick),
        RowAction(name = "delete", icon = Delete, onClick = ::handleDeleteClick, iconColor = IconButtonColor.warning),
    )

    val toolbarActions = listOf(
        ToolbarAction(
            name = "import",
            icon = GetApp,
            onClick = ::handleImportClick,
            requiresSelection = true
        ),
        ToolbarAction(
            name = "delete",
            icon = Delete,
            onClick = ::handleDeleteClick,
            requiresSelection = true,
            iconColor = IconButtonColor.error
        ),
        ToolbarAction(
            name = "refresh",
            icon = Refresh,
            onClick = ::handleRefreshClick,
            fetchingAnimation = import.isFetching
        ),
    )

    val table = useTable(
        options = jso {
            data = useMemo(import.data) { import.data?.toTypedArray() ?: emptyArray() }
            columns = useMemo { COLUMNS }
        },
        useSortBy,
        usePagination,
        useRowSelect,
        useCheckboxSelect,
        useColumnOrder,
        useRowActions(rowActions),
    )

    Box {
        Paper {
            EnhancedTable {
                title = "Import Table"
                tableInstance = table.unsafeCast<TableInstance<Any>>()
                this.toolbarActions = toolbarActions
                selectedRows = tableInstance.selectedFlatRows
            }
        }
    }

    if (currentTrack != null) {
        TrackEditDialog {
            open = dialogOpen
            handleClose = { setDialogOpen(false) }
            track = currentTrack
        }
    }

}