package uk.dioxic.muon.component.page

import js.core.jso
import kotlinx.browser.window
import mui.icons.material.*
import mui.material.*
import mui.system.sx
import react.VFC
import react.useMemo
import react.useState
import tanstack.react.table.renderCell
import tanstack.react.table.renderHeader
import tanstack.react.table.useReactTable
import tanstack.table.core.*
import tanstack.table.core.SortDirection
import uk.dioxic.muon.Routes
import uk.dioxic.muon.component.dialog.ImportDialog
import uk.dioxic.muon.component.dialog.TrackEditDialog
import uk.dioxic.muon.component.table.TableToolbar
import uk.dioxic.muon.component.table.actions.RowAction
import uk.dioxic.muon.component.table.actions.ToolbarAction
import uk.dioxic.muon.component.table.columns
import uk.dioxic.muon.hook.*
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.model.Tracks
import web.cssom.Cursor
import kotlin.time.Duration.Companion.seconds

val ImportPage = VFC {
    val settings = useSettingsFetch().data
    val tracks = useImportFetch()
    val reload = useImportReload()
    val delete = useImportDelete()
    val import = useImportMutation()
    val (editDialogOpen, setEditDialogOpen) = useState(false)
    val (importDialogOpen, setImportDialogOpen) = useState(false)
    val (selectedRows, setSelectedRows) = useState<Tracks>(emptyList())
    val (sorting, setSorting) = useState<SortingState>(emptyArray())

    fun handleEditClick(tracks: Tracks) {
        setSelectedRows(tracks)
        setEditDialogOpen(true)
    }

    fun handleDeleteClick(tracks: Tracks) {
        tracks.forEach { delete(it) }
    }

    fun handleImportClick(tracks: Tracks) {
        setSelectedRows(tracks)
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

    fun handlePlay(track: Track) {
        window.open(Routes.trackAudio(track), "_blank")?.focus()
    }

    val rowActions = listOf(
        RowAction(name = "edit", icon = Edit, onClick = ::handleRowEditClick),
        RowAction(name = "import", icon = GetApp, onClick = ::handleRowImportClick),
        RowAction(name = "Play", icon = PlayCircle, onClick = ::handlePlay),
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

    var columnsSSS = arrayOf<ColumnDef<Track, Any>>(
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
            id = "filename"
            header = StringOrTemplateHeader("Filename")
            accessorFn = { row, _ -> row.filename }
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
    )

    val columnDefs = columns<Track> {
        column(
            id = "title",
            header = "Title",
            accessor = { title },
        )
        column(
            id = "artist",
            header = "Artist",
            accessor = { artist },
        )
        column(
            id = "genre",
            header = "Genre",
            accessor = { genre },
        )
        column(
            id = "album",
            header = "Album",
            accessor = { album },
        )
        column(
            id = "lyricist",
            header = "Lyricist",
            accessor = { lyricist },
        )
        column(
            id = "comment",
            header = "Comment",
            accessor = { comment },
        )
        column(
            id = "bitrate",
            header = "Bitrate",
            accessor = { bitrate },
        )
        if (settings?.standardiseFilenames == false) {
            column(
                id = "filename",
                header = "Filename",
                accessor = { filename }
            )
        }
        column(
            id = "year",
            header = "Year",
            accessor = { year },
        )
        column(
            id = "length",
            header = "Length",
            accessor = { length.seconds.toString() },
        )
        column(
            id = "type",
            header = "Type",
            accessor = { type },
        )
    }

    val table = useReactTable<Track>(
        options = jso {
            this.data = tracks.data?.toTypedArray() ?: emptyArray()
            this.columns = useMemo { columnDefs }
            this.state = jso {
                this.sorting = sorting
            }
            this.onSortingChange = { setSorting }
            this.getCoreRowModel = getCoreRowModel()
            this.getSortedRowModel = getSortedRowModel()
            this.debugTable = true
        }
    )

    Box {
        Paper {
//            EnhancedTable {
//                title = "Import Table"
//                tableInstance = table
//                this.toolbarActions = toolbarActions
//                selectedRows = tableInstance.selectedFlatRows
//                columnCount = columns.size + 3 // check, expand + actions column
//            }
            TableContainer {
                TableToolbar {
                    title = "Import Table"
                    actions = toolbarActions
                    selected = emptyArray()
                }

                Table {
                    size = Size.small
                    stickyHeader = true

                    TableHead {
                        table.getHeaderGroups().forEach { headerGroup ->
                            TableRow {
                                headerGroup.headers.forEach { header ->
                                    TableCell {
                                        if (header.column.getCanSort()) {
                                            sx {
                                                cursor = Cursor.pointer
                                            }
                                        }

                                        +renderHeader(header)

//                                        if (!header.isPlaceholder) {

//                                            println("canSort: ${header.column.getCanSort()}")
//                                            println("isSorted: ${header.column.getIsSorted()}")

//                                            if (header.column.getCanSort()) {
//                                                TableSortLabel {
//                                                    onClick = header.column.getToggleSortingHandler()
//                                                    active = header.column.getIsSorted() != null
//                                                    direction = when (header.column.getIsSorted()) {
//                                                        SortDirection.asc -> TableSortLabelDirection.asc
//                                                        else -> TableSortLabelDirection.desc
//                                                    else -> null
//                                                    }
//                                                }
//                                            }
//                                        }
                                    }
                                }
                            }
                        }
                    }

                    TableBody {
                        table.getRowModel().rows.forEach { row ->
                            TableRow {
                                row.getVisibleCells().forEach { cell ->
                                    TableCell {
                                        +renderCell(cell)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedRows.isNotEmpty()) {
        TrackEditDialog {
            open = editDialogOpen
            this.tracks = selectedRows
            handleClose = { setEditDialogOpen(false) }
        }
        ImportDialog {
            open = importDialogOpen
            this.tracks = selectedRows
            handleImport = { import.mutate(it, jso()) }
            handleClose = { setImportDialogOpen(false) }
        }
    }

}