package uk.dioxic.muon.component.page

import kotlinx.js.jso
import mui.icons.material.Delete
import mui.icons.material.Edit
import mui.icons.material.GetApp
import mui.icons.material.Refresh
import mui.material.Box
import mui.material.IconButtonColor
import mui.material.Paper
import react.VFC
import react.table.*
import react.useMemo
import react.useState
import uk.dioxic.muon.component.dialog.ImportDialog
import uk.dioxic.muon.component.dialog.TrackEditDialog
import uk.dioxic.muon.component.table.EnhancedTable
import uk.dioxic.muon.component.table.actions.RowAction
import uk.dioxic.muon.component.table.actions.ToolbarAction
import uk.dioxic.muon.component.table.plugin.useCheckboxSelect
import uk.dioxic.muon.component.table.plugin.useRowActions
import uk.dioxic.muon.hook.*
import uk.dioxic.muon.model.FileType
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.model.Tracks
import kotlin.time.Duration.Companion.seconds

val ImportPage = VFC {
    val settings = useSettingsFetch().data
    val tracks = useImportFetch()
    val reload = useImportReload()
    val delete = useImportDelete()
    val import = useImportMutation()
    val (editDialogOpen, setEditDialogOpen) = useState(false)
    val (importDialogOpen, setImportDialogOpen) = useState(false)
    val (selected, setSelected) = useState<Tracks>(emptyList())

    fun handleEditClick(tracks: Tracks) {
        setSelected(tracks)
        setEditDialogOpen(true)
    }

    fun handleDeleteClick(tracks: Tracks) {
        tracks.forEach { delete(it) }
    }

    fun handleImportClick(tracks: Tracks) {
        setSelected(tracks)
        val hasDuplicates = tracks.count { !it.duplicates.isNullOrEmpty() } > 0

        if (hasDuplicates) {
            setImportDialogOpen(true)
        } else {
            import.mutate(tracks, jso())
        }
    }

    fun handleRowEditClick(track: Track) = handleEditClick(listOf(track))
    fun handleRowImportClick(track: Track) = handleImportClick(listOf(track))
    fun handleRowDeleteClick(track: Track) = handleDeleteClick(listOf(track))

    @Suppress("UNUSED_PARAMETER")
    fun handleRefreshClick(rows: Tracks) {
        reload()
    }

    val rowActions = listOf(
        RowAction(name = "edit", icon = Edit, onClick = ::handleRowEditClick),
        RowAction(name = "import", icon = GetApp, onClick = ::handleRowImportClick),
        RowAction(name = "delete", icon = Delete, onClick = ::handleRowDeleteClick, iconColor = IconButtonColor.error),
    )

    val toolbarActions = listOf(
        ToolbarAction(
            name = "edit",
            icon = Edit,
            onClick = ::handleEditClick,
            requiresSelection = true
        ),
        ToolbarAction(
            name = "import",
            icon = GetApp,
            onClick = ::handleImportClick,
            requiresSelection = true,
            fetchingAnimation = import.isLoading
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

    val columnDefinitions = columns<Track> {
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
        if (settings?.standardiseFilenames == false) {
            column<String> {
                header = "Filename"
                accessorFunction = { it.filename }
            }
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

    val table = useTable(
        options = jso {
            data = useMemo(tracks.data) { tracks.data?.toTypedArray() ?: emptyArray() }
            columns = useMemo(settings?.standardiseFilenames) { columnDefinitions }
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
                tableInstance = table //.unsafeCast<TableInstance<Track>>()
                this.toolbarActions = toolbarActions //.unsafeCast<List<ToolbarAction<Track>>>()
                selectedRows = tableInstance.selectedFlatRows
                columnCount = columnDefinitions.size + 3 // check, expand + actions column
            }
        }
    }

    if (selected.isNotEmpty()) {
        TrackEditDialog {
            open = editDialogOpen
            this.tracks = selected
            handleClose = { setEditDialogOpen(false) }
        }
        ImportDialog {
            open = importDialogOpen
            this.tracks = selected
            handleImport = { import.mutate(it, jso()) }
            handleClose = { setImportDialogOpen(false) }
        }
    }

}