package uk.dioxic.muon.component.page

import js.core.jso
import kotlinx.browser.window
import mui.icons.material.*
import mui.material.*
import mui.system.sx
import react.*
import tanstack.react.table.renderCell
import tanstack.react.table.renderHeader
import tanstack.react.table.useReactTable
import tanstack.table.core.*
import uk.dioxic.muon.Routes
import uk.dioxic.muon.common.getIsAnyRowsSelected
import uk.dioxic.muon.common.getSelectedData
import uk.dioxic.muon.component.dialog.ImportDialog
import uk.dioxic.muon.component.dialog.TrackEditDialog
import uk.dioxic.muon.component.table.TableToolbar
import uk.dioxic.muon.component.table.actions.RowAction
import uk.dioxic.muon.component.table.actions.ToolbarAction
import uk.dioxic.muon.component.table.columns.checkboxCellTemplate
import uk.dioxic.muon.component.table.columns.checkboxHeaderTemplate
import uk.dioxic.muon.component.table.columns.rowActionTemplate
import uk.dioxic.muon.hook.*
import uk.dioxic.muon.model.Track
import web.cssom.Cursor
import kotlin.time.Duration.Companion.seconds

val ImportPage = VFC {
    val settings = useSettingsFetch()
    val fetch = useImportFetchOld()
    val tracks = useMemo(fetch) { fetch.data ?: emptyArray() }
    val reload = useImportReload()
    val delete = useImportDelete()
    val import = useImportMutation()
    val (editDialogTracks, setEditDialogTracks) = useState<Array<Track>>(emptyArray())
    val (importDialogTracks, setImportDialogTracks) = useState<Array<Track>>(emptyArray())
//    val (sorting, setSorting) = useState<SortingState>(emptyArray())

    fun handleRowPlayClick(track: Track) {
        window.open(Routes.trackAudio(track), "_blank")?.focus()
    }

    val rowActions = listOf(
        RowAction(name = "edit", icon = Edit, onClick = { setEditDialogTracks(arrayOf(it)) }),
        RowAction(name = "import", icon = GetApp, onClick = { setImportDialogTracks(arrayOf(it)) }),
        RowAction(name = "Play", icon = PlayCircle, onClick = ::handleRowPlayClick),
        RowAction(name = "delete", icon = Delete, onClick = { delete(it) }, iconColor = IconButtonColor.error),
    )

    val columnsDefs = useMemo {
        arrayOf<ColumnDef<Track, Any>>(
            jso {
                id = "checkbox"
                header = StringOrTemplateHeader(checkboxHeaderTemplate())
                cell = checkboxCellTemplate()
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
            jso {
                id = "action"
                header = StringOrTemplateHeader("Action")
                cell = rowActionTemplate(rowActions)
            }
        )
    }

    val table = useReactTable<Track>(
        options = jso {
            data = tracks
            columns = columnsDefs
            enableRowSelection = { _ -> true }
            enableMultiRowSelection = { _ -> true }
//            onRowSelectionChange = { println("row selection") }
//            this.onSortingChange = { updater -> setSorting.invoke(updater)}
//            state = jso {
//                this.rowSelection = rowSelection
//                this.sorting = sorting
//            }
//            this.onSortingChange = { setSorting }
            getCoreRowModel = getCoreRowModel()
//            this.getSortedRowModel = getSortedRowModel()
//            this.debugTable = true
        }
    )

    val toolbarActions = listOf(
        ToolbarAction(
            name = "refresh",
            icon = Refresh,
            onClick = { reload() },
            fetchingAnimation = fetch.isLoading
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
            onClick = { table.getSelectedData().forEach {
                track -> delete(track)
            } },
            iconColor = IconButtonColor.error,
            visible = table.getIsAnyRowsSelected()
        ),
    )

//    println("render")
//    println("table selection: " + table.getSelectedRowModel().rows.map { it.original.title })
//    println("selection state: $editDialogTracks")
//    println("selection state is empty: ${editDialogTracks.isEmpty()}")


    Box {
        Paper {
            TableContainer {
                TableToolbar {
                    title = "Import Table"
                    actions = toolbarActions
                    selectedCount = table.getSelectedRowModel().rows.size
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
                                onClick = { event ->
                                    if (row.getCanSelect()) {
                                        row.getToggleSelectedHandler()(event)
                                    }
                                }
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