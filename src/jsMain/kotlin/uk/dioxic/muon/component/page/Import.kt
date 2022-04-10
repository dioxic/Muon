package uk.dioxic.muon.component.page

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
import uk.dioxic.muon.component.table.actions.RowAction
import uk.dioxic.muon.component.table.actions.ToolbarAction
import uk.dioxic.muon.component.table.plugin.useCheckboxSelect
import uk.dioxic.muon.component.table.plugin.useRowActions
import uk.dioxic.muon.hook.useImportDelete
import uk.dioxic.muon.hook.useImportFetch
import uk.dioxic.muon.hook.useImportReload
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
    val tracks = useImportFetch()
    val reload = useImportReload()
    val delete = useImportDelete()
    val (dialogOpen, setDialogOpen) = useState(false)
    val (currentTrack, setCurrentTrack) = useState<Track?>(null)

    fun handleEditClick(row: Row<Track>) {
        setCurrentTrack(row.original)
        setDialogOpen(true)
    }

    fun handleDeleteClick(vararg rows: Row<Track>) {
        rows.forEach { delete(it.original) }
    }

    fun handleImportClick(vararg rows: Row<Track>) {
        println("handleImport for $rows")
    }

    @Suppress("UNUSED_PARAMETER")
    fun handleRefreshClick(vararg rows: Row<Track>) {
        reload()
    }

    val rowActions = listOf(
        RowAction(name = "edit", icon = Edit, onClick = ::handleEditClick),
        RowAction(name = "import", icon = GetApp, onClick = ::handleImportClick),
        RowAction(name = "delete", icon = Delete, onClick = ::handleDeleteClick, iconColor = IconButtonColor.error),
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
            fetchingAnimation = tracks.isFetching
        ),
    )

    val table = useTable(
        options = jso {
            data = useMemo(tracks.data) { tracks.data?.toTypedArray() ?: emptyArray() }
            columns = useMemo { COLUMNS }
        },
        useSortBy,
        useRowSelect,
        useCheckboxSelect,
        useColumnOrder,
        useRowActions(rowActions),
    )

    Box {
        Paper {
            EnhancedTable {
                title = "Import Table"
                tableInstance = table
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