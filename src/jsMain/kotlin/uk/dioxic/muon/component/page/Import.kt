package uk.dioxic.muon.component.page

import csstype.px
import kotlinx.js.ReadonlyArray
import kotlinx.js.jso
import mui.icons.material.Delete
import mui.icons.material.GetApp
import mui.icons.material.Refresh
import mui.material.*
import mui.system.sx
import react.FC
import react.Props
import react.table.*
import react.useMemo
import uk.dioxic.muon.component.table.EnhancedTableToolbar
import uk.dioxic.muon.component.table.ToolbarAction
import uk.dioxic.muon.component.table.plugin.useCheckboxSelect
import uk.dioxic.muon.hook.useImport
import uk.dioxic.muon.hook.useReloadImport
import uk.dioxic.muon.hook.useSettings
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
    val settings = useSettings().data
    val import = useImport().data
    val reloadImport = useReloadImport()

    fun handleEditClick(id: String) {
        println("handleEdit for $id")
    }

    fun handleDeleteClick(id: String) {
        println("handleDelete for $id")
    }

    fun handleDeleteClick(selected: ReadonlyArray<Row<*>>) {
        println("handleDelete for $selected")
    }

    fun handleImportClick(id: String) {
        println("handleImport for $id")
    }

    fun handleFilterClick(selected: ReadonlyArray<Row<*>>) {
        println("handleFilter for $selected")
    }

    @Suppress("UNUSED_PARAMETER")
    fun handleRefreshClick(selected: ReadonlyArray<Row<*>>) {
        println("handleRefresh")
        reloadImport()
    }

//    val rowActions = listOf(
//        RowAction(name = "edit", icon = Edit, onClick = ::handleEditClick),
//        RowAction(name = "import", icon = GetApp, onClick = ::handleImportClick),
//        RowAction(name = "delete", icon = Delete, onClick = ::handleDeleteClick),
//    )

    val toolbarActions = listOf(
        ToolbarAction(name = "import", icon = GetApp, onClick = ::handleFilterClick, requiresSelection = true),
        ToolbarAction(name = "delete", icon = Delete, onClick = ::handleDeleteClick, requiresSelection = true),
        ToolbarAction(name = "refresh", icon = Refresh, onClick = ::handleRefreshClick),
    )

    val table = useTable<Track>(
        options = jso {
            data = useMemo(import) { import?.toTypedArray() ?: emptyArray() }
            columns = useMemo { COLUMNS }
//            defaultColumn = column {
//                 cellFunction =  { cellProps ->  editableCell(cellProps) }
//            }
        },
        useSortBy,
        useRowSelect,
        useCheckboxSelect,
        useColumnOrder,
    )

    Box {
        Paper {
            TableContainer {
                EnhancedTableToolbar {
                    title = "Import Table"
                    actions = toolbarActions
                    selected = table.selectedFlatRows
                }

                Table {
                    size = Size.small

                    +table.getTableProps()

                    TableHead {
                        table.headerGroups.forEach { headerGroup ->
                            TableRow {
                                +headerGroup.getHeaderGroupProps()

                                headerGroup.headers.forEach { h ->
                                    val originalHeader = h.placeholderOf
                                    val header = originalHeader ?: h

                                    TableCell {
                                        val isCheckboxCell = (header.id == "selection")

                                        if (isCheckboxCell) {
                                            +header.getHeaderProps()
                                        } else {
                                            sx {
                                                minWidth = 120.px
                                            }
                                            +header.getHeaderProps(header.getSortByToggleProps())
                                        }

                                        +header.render(RenderType.Header)

                                        if (!isCheckboxCell) {
                                            TableSortLabel {
                                                active = header.isSorted
                                                direction = if (header.isSortedDesc)
                                                    TableSortLabelDirection.desc
                                                else
                                                    TableSortLabelDirection.asc
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    TableBody {
                        table.rows.forEach { row ->
                            table.prepareRow(row)

                            TableRow {
                                +row.getRowProps()

                                row.cells.forEach { cell ->
                                    TableCell {
                                        +cell.getCellProps()
                                        +cell.render(RenderType.Cell)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

//    val tableComponent = useMemo { enhancedReactTable<Track>() }
//    val toolbarComponent = useMemo { enhancedTableToolbar<Track>() }
//
//    child(
//        createElement(tableComponent, jso {
//            this.table = table
//            this.toolbar = createElement(toolbarComponent, jso {
//                title = "Import Table"
//                actions = toolbarActions
//                selected = table.selectedFlatRows
//            })
//            title = "Import Table"
//            data = useMemo(import) { import?.toTypedArray() ?: emptyArray() }
//            columns = useMemo { COLUMNS }
//            this.toolbarActions = toolbarActions
//            minWidth = 120.px
//        })
//    )

//    EnhancedReactTable {
//        AriaRole.table = table.asDynamic().unsafeCast<TableInstance<Any>>()
//        title = "React Import Table"
//        data = useMemo(import) { import?.toTypedArray() ?: emptyArray() }
//        columns = useMemo { COLUMNS }
//        this.toolbarActions = toolbarActions
//        minWidth = 150.px
//    }

}