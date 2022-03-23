package uk.dioxic.muon.component.page

import csstype.*
import kotlinx.js.jso
import mui.icons.material.*
import mui.material.*
import mui.material.Size
import mui.system.sx
import react.*
import react.dom.aria.*
import react.dom.html.ReactHTML
import uk.dioxic.muon.common.toTableSortLabel
import uk.dioxic.muon.component.ThemeContext
import uk.dioxic.muon.config.defaultImportTableColumns
import uk.dioxic.muon.model.*

val ImportPage = FC<Props> {
    val (selected, setSelected) = useState(emptyList<String>())
    val (data, setData) = useState(testData)
    val (columns, setColumns) = useState(defaultImportTableColumns)
    val (order, setOrder) = useState(SortDirection.asc)
    val (orderBy, setOrderBy) = useState("title")

    fun handleRowClick(id: String) =
        setSelected(
            if (selected.contains(id)) {
                selected.filterNot { it == id }
            } else {
                selected + id
            }
        )

    fun handleSelectAllClick(selectAll: Boolean) {
        if (selectAll) {
            setSelected(data.map { row -> row.id })
        } else {
            setSelected(emptyList())
        }
    }

    fun handleEditClick(id: String) {
        println("handleEdit")
    }

    fun handleDeleteClick(id: String) {
        println("handleDelete")
    }

    fun handleImportClick(id: String) {
        println("handleImport")
    }

    fun handleRequestSort(id: String) {
        setOrder(
            if (orderBy == id && order == SortDirection.asc) {
                SortDirection.desc
            } else {
                SortDirection.asc
            }
        )
        setOrderBy(id)
    }

    val rowActions = listOf(
        RowAction(id = "edit", icon = Edit, onClick = ::handleEditClick),
        RowAction(id = "import", icon = GetApp, onClick = ::handleImportClick),
        RowAction(id = "delete", icon = Delete, onClick = ::handleDeleteClick),
    )

    Box {
        Paper {
            TableToolbar {
                numSelected = selected.size
            }

            TableContainer {
                component = Paper.create().type

                Table {
                    sx { minWidth = 650.px }
//            stickyHeader = true
                    ariaLabel = "music table"

                    EnhancedTableHead {
                        numSelected = selected.size
                        rowCount = testData.size
                        this.columns = columns.toColumns()
                        this.order = order
                        this.orderBy = orderBy
                        hasActions = rowActions.isNotEmpty()
                        onSelectAllClick = ::handleSelectAllClick
                        onRequestSort = ::handleRequestSort
                    }

                    EnhancedTableBody {
                        this.selected = selected
                        this.columns = columns.toColumns()
                        this.rows = data.toRows()
                        this.order = order
                        this.orderBy = orderBy
                        this.rowActions = rowActions
                        onRowClick = ::handleRowClick
                    }

                }
            }
        }
    }
}

external interface ToolbarProps : Props {
    var numSelected: Int
}

val TableToolbar = FC<ToolbarProps> { props ->
    val theme by useContext(ThemeContext)

    Toolbar {
        sx {
            if (props.numSelected > 0) {
                backgroundColor = Color(theme.palette.secondary.main)
                color = Color(theme.palette.secondary.contrastText)
            }
        }

        Typography {
            sx { flexGrow = number(1.0) }
            component = ReactHTML.div
            variant = "h6"

            if (props.numSelected > 0) {
                +"${props.numSelected} selected"
            } else {
                +"Music Import"
            }
        }

        if (props.numSelected > 0) {
            Tooltip {
                title = ReactNode("Delete")

                IconButton {
                    color = IconButtonColor.inherit

                    Delete()
                }
            }
        } else {
            Tooltip {
                title = ReactNode("Filter")

                IconButton {
                    color = IconButtonColor.inherit

//                    Filter()
                    Alarm()
                }
            }
        }
    }
}

external interface EnhancedTableHeadProps : Props {
    var numSelected: Int
    var rowCount: Int
    var order: SortDirection
    var orderBy: String
    var columns: List<TableColumn>
    var hasActions: Boolean
    var onRequestSort: (String) -> Unit
    var onSelectAllClick: (Boolean) -> Unit
}

val EnhancedTableHead = FC<EnhancedTableHeadProps> { props ->
    TableHead {
        TableRow {
            TableCell {
                padding = TableCellPadding.checkbox
                Checkbox {
                    color = CheckboxColor.primary
                    indeterminate = (props.numSelected > 0 && props.numSelected < props.rowCount)
                    checked = (props.rowCount > 0 && props.numSelected == props.rowCount)
                    ariaLabel = "select all music"
                    onChange = { _, checked -> props.onSelectAllClick(checked) }
                }
            }

            props.columns
                .filter { it.visible }
                .forEach { column ->
                    val orderedColumn = props.orderBy == column.id

                    TableCell {
                        key = column.id
                        align = column.align
                        style = jso {
                            top = 57.px
                            minWidth = column.minWidth
                        }
                        sortDirection = if (orderedColumn) props.order else SortDirection.`false`

                        TableSortLabel {
                            onClick = { _ -> props.onRequestSort(column.id) }
                            if (orderedColumn) {
                                active = true
                                direction = props.order.toTableSortLabel()
                            }

                            +column.label
                        }
                    }
                }
            if (props.hasActions) {
                TableCell {
                    key = "action"
                    align = TableCellAlign.center
                    padding = TableCellPadding.checkbox

                    +"Actions"
                }
            }
        }
    }
}

external interface EnhancedTableBodyProps : Props {
    var selected: List<String>
    var rows: List<ImportTableRow>
    var columns: List<TableColumn>
    var order: SortDirection
    var orderBy: String
    var onRowClick: (String) -> Unit
    var rowActions: List<RowAction>
}

val EnhancedTableBody = FC<EnhancedTableBodyProps> { props ->
    TableBody {

        props.rows
            .sortedWith(comparator(props.order, props.orderBy))
            .forEach { row ->
                val isRowSelected = props.selected.contains(row.id)

                TableRow {
                    key = row.id
                    hover = true
                    role = AriaRole.checkbox
                    ariaChecked = if (isRowSelected) AriaChecked.`true` else AriaChecked.`false`
                    tabIndex = -1
                    onClick = { _ -> props.onRowClick(row.id) }

                    TableCell {
                        padding = TableCellPadding.checkbox

                        Checkbox {
                            color = CheckboxColor.primary
                            checked = isRowSelected
                            ariaLabelledBy = "enhanced-table-checkbox-${row.id}"
                        }
                    }

                    props.columns
                        .filter { it.visible }
                        .forEach { column ->
                            val value: String = row.asDynamic()[column.id].unsafeCast<String>()

                            TableCell {
                                align = column.align

                                +value
                            }
                        }

                    if (props.rowActions.isNotEmpty()) {
                        TableCell {
                            padding = TableCellPadding.normal
                            align = TableCellAlign.center
                            size = Size.small

                            Box {
                                component = ReactHTML.div
                                sx {
                                    display = Display.flex
                                }

                                props.rowActions.forEach {
                                    IconButton {
                                        id = "${it.id}-${row.id}"
                                        size = it.size
                                        onClick = { event ->
                                            event.stopPropagation()
                                            it.onClick(row.id)
                                        }

                                        it.icon()
                                    }
                                }
                            }
                        }
                    }
                }
            }
    }
}

data class RowAction(
    val id: String,
    val size: Size = Size.small,
    val icon: SvgIconComponent,
    val onClick: (String) -> Unit,
)

private fun comparator(order: SortDirection, orderBy: String) = Comparator { a: ImportTableRow, b: ImportTableRow ->
    val aColumn = getSortColumn(a, orderBy)
    val bColumn = getSortColumn(b, orderBy)

    val res = when {
        aColumn < bColumn -> -1
        aColumn > bColumn -> 1
        else -> 0
    }

    if (order == SortDirection.asc) res else -res
}

private fun getSortColumn(row: ImportTableRow, column: String): dynamic {
    val sortColumn = row.asDynamic()["${column}Sort"]
    return if (sortColumn != undefined) {
        sortColumn
    } else {
        row.asDynamic()[column]
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