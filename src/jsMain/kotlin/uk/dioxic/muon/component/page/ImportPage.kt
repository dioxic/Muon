package uk.dioxic.muon.component.page

import js.core.jso
import mui.icons.material.*
import mui.material.Box
import mui.material.IconButtonColor
import mui.material.Paper
import mui.material.TableContainer
import react.VFC
import react.useContext
import react.useMemo
import react.useState
import tanstack.react.table.useReactTable
import tanstack.table.core.ColumnDef
import tanstack.table.core.StringOrTemplateHeader
import tanstack.table.core.getCoreRowModel
import tanstack.table.core.getSortedRowModel
import uk.dioxic.muon.common.getIsAnyRowsSelected
import uk.dioxic.muon.common.getSelectedData
import uk.dioxic.muon.component.dialog.ImportDialog
import uk.dioxic.muon.component.dialog.TrackEditDialog
import uk.dioxic.muon.component.table.BasicTable
import uk.dioxic.muon.component.table.ActionTableToolbar
import uk.dioxic.muon.component.table.actions.RowAction
import uk.dioxic.muon.component.table.actions.ToolbarAction
import uk.dioxic.muon.component.table.columns.checkboxCellTemplate
import uk.dioxic.muon.component.table.columns.checkboxHeaderTemplate
import uk.dioxic.muon.component.table.columns.rowActionTemplate
import uk.dioxic.muon.context.IsPlayingContext
import uk.dioxic.muon.context.PlayTrackContext
import uk.dioxic.muon.context.SettingsContext
import uk.dioxic.muon.context.TogglePlayStateContext
import uk.dioxic.muon.hook.*
import uk.dioxic.muon.model.Track
import kotlin.time.Duration.Companion.seconds

val ImportPage = VFC {
    val settings = useContext(SettingsContext)
    val fetch = useImportFetch()
    val tracks = fetch.data ?: emptyArray()
    val reload = useImportReload()
    val delete = useImportDelete()
    val import = useImportMutation()
    val (editDialogTracks, setEditDialogTracks) = useState<Array<Track>>(emptyArray())
    val (importDialogTracks, setImportDialogTracks) = useState<Array<Track>>(emptyArray())
    val isPlaying = useContext(IsPlayingContext)!!
    val (playTrack, setPlayTrack) = useContext(PlayTrackContext)!!
    val togglePlayState = useContext(TogglePlayStateContext)!!

    fun handlePlayClick(track: Track) {
        if (track != playTrack) {
            setPlayTrack(track)
        }
        else {
            togglePlayState()
        }
    }

    val rowActions = listOf(
        RowAction(
            name = "edit",
            icon = Edit,
            onClick = { setEditDialogTracks(arrayOf(it)) }
        ),
        RowAction(
            name = "import",
            icon = GetApp,
            onClick = { setImportDialogTracks(arrayOf(it)) }
        ),
        RowAction(
            name = "play",
            iconFn = { track ->
                if (isPlaying && playTrack == track) {
                    PauseCircle
                } else {
                    PlayCircle
                }
            },
            onClick = ::handlePlayClick
        ),
        RowAction(
            name = "delete",
            icon = Delete,
            onClick = { delete(it) },
            iconColor = IconButtonColor.error
        ),
    )

    val columnsDefs = useMemo(isPlaying, playTrack, settings) {
        mutableListOf<ColumnDef<Track, Any>>(
            jso {
                id = "checkbox"
                header = StringOrTemplateHeader(checkboxHeaderTemplate())
                cell = checkboxCellTemplate()
                enableSorting = false
            },
            jso {
                id = "title"
                header = StringOrTemplateHeader("Title")
                accessorFn = { row, _ -> row.title }
            },
            jso {
                id = "artist"
                header = StringOrTemplateHeader("Artist")
                accessorFn = { row, _ -> row.artist }
            },
            jso {
                id = "genre"
                header = StringOrTemplateHeader("Genre")
                accessorFn = { row, _ -> row.genre }
            },
            jso {
                id = "album"
                header = StringOrTemplateHeader("Album")
                accessorFn = { row, _ -> row.album }
            },
            jso {
                id = "lyricist"
                header = StringOrTemplateHeader("Lyricist")
                accessorFn = { row, _ -> row.lyricist }
            },
            jso {
                id = "comment"
                header = StringOrTemplateHeader("Comment")
                accessorFn = { row, _ -> row.comment }
            },
            jso {
                id = "bitrate"
                header = StringOrTemplateHeader("Bitrate")
                accessorFn = { row, _ -> row.bitrate.toString() }
            },
            jso {
                id = "year"
                header = StringOrTemplateHeader("Year")
                accessorFn = { row, _ -> row.year }
            },
            jso {
                id = "length"
                header = StringOrTemplateHeader("Length")
                accessorFn = { row, _ -> row.length.seconds.toString() }
            },
            jso {
                id = "type"
                header = StringOrTemplateHeader("Type")
                accessorFn = { row, _ -> row.type.name }
            },
            jso {
                id = "action"
                header = StringOrTemplateHeader("Action")
                cell = rowActionTemplate(rowActions)
                enableSorting = false
            }
        ).apply {
            if (settings?.standardiseFilenames == false) {
                add(jso {
                    id = "filename"
                    header = StringOrTemplateHeader("Filename")
                    accessorFn = { row, _ -> row.filename }
                })
            }
        }.toTypedArray()
    }

    val table = useReactTable<Track>(
        options = jso {
            data = tracks
            columns = columnsDefs
            enableRowSelection = { _ -> true }
            enableMultiRowSelection = { _ -> true }
            getCoreRowModel = getCoreRowModel()
            getSortedRowModel = getSortedRowModel()
//            this.debugTable = true
        }
    )

    val toolbarActions = listOf(
        ToolbarAction(
            name = "refresh",
            icon = Refresh,
            onClick = { reload() },
            fetchingAnimation = fetch.isLoading || fetch.isFetching
        ),
        ToolbarAction(
            name = "edit",
            icon = Edit,
            onClick = {
                setEditDialogTracks(table.getSelectedData())
            },
            visible = table.getIsAnyRowsSelected()
        ),
        ToolbarAction(
            name = "import",
            icon = GetApp,
            onClick = { setImportDialogTracks(table.getSelectedData()) },
            fetchingAnimation = import.isLoading,
            visible = table.getIsAnyRowsSelected() || import.isLoading
        ),
        ToolbarAction(
            name = "delete",
            icon = Delete,
            onClick = {
                table.getSelectedData().forEach { track ->
                    delete(track)
                }
            },
            iconColor = IconButtonColor.error,
            visible = table.getIsAnyRowsSelected()
        ),
    )

    Box {
        Paper {
            TableContainer {
                ActionTableToolbar {
                    title = "Import Table"
                    actions = toolbarActions
                    selectedCount = table.getSelectedRowModel().rows.size
                }
                BasicTable {
                    this.table = table
                    this.selectable = true
                    this.sortable = true
                }
            }
        }
    }

    TrackEditDialog {
        open = editDialogTracks.isNotEmpty()
        this.tracks = editDialogTracks
        handleClose = { setEditDialogTracks(emptyArray()) }
    }
    ImportDialog {
        open = importDialogTracks.isNotEmpty()
        this.tracks = importDialogTracks
        handleImport = { import.mutate(it, jso()) }
        handleClose = { setImportDialogTracks(emptyArray()) }
    }

}