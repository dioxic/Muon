package uk.dioxic.muon.component

import csstype.Color
import csstype.Display
import csstype.number
import csstype.px
import kotlinx.js.jso
import mui.icons.material.SvgIconComponent
import mui.material.*
import mui.system.sx
import react.*
import react.dom.aria.*
import react.dom.html.ReactHTML
import uk.dioxic.muon.model.TableColumn
import uk.dioxic.muon.model.TableRow

external interface EnhancedTableProps : Props {
    var title: String
    var rows: List<TableRow>
    var columns: List<TableColumn>
    var rowActions: List<RowAction>
    var toolbarActions: List<ToolbarAction>
    var selectable: Boolean
    var sortable: Boolean
}

val EnhancedTable = FC<EnhancedTableProps> { props ->
    val (selected, setSelected) = useState(emptyList<String>())
    val (order, setOrder) = useState(SortDirection.asc)
    val (orderBy, setOrderBy) = useState(props.columns[0].id)

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
            setSelected(props.rows.map { row -> row.id })
        } else {
            setSelected(emptyList())
        }
    }

    Box {
        Paper {
            TableToolbar {
                title = props.title
                actions = props.toolbarActions
                this.selected = selected
            }

            TableContainer {
                Table {
                    sx { minWidth = 650.px }

                    EnhancedTableHead {
                        rowCount = props.rows.size
                        columns = props.columns
                        sortable = props.sortable
                        selectable = props.selectable
                        hasActions = props.rowActions.isNotEmpty()
                        numSelected = selected.size
                        this.order = order
                        this.orderBy = orderBy
                        onSelectAllClick = ::handleSelectAllClick
                        onRequestSort = ::handleRequestSort
                    }

                    EnhancedTableBody {
                        columns = props.columns
                        rows = props.rows
                        selectable = props.selectable
                        actions = props.rowActions
                        this.selected = selected
                        this.order = order
                        this.orderBy = orderBy
                        onRowClick = ::handleRowClick
                    }
                }
            }
        }
    }
}

external interface ToolbarProps : Props {
    var title: String
    var selected: List<String>
    var actions: List<ToolbarAction>
}

val TableToolbar = FC<ToolbarProps> { props ->
    val theme by useContext(ThemeContext)

    Toolbar {
        sx {
            if (props.selected.isNotEmpty()) {
                backgroundColor = Color(theme.palette.secondary.main)
                color = Color(theme.palette.secondary.contrastText)
            }
        }

        Typography {
            sx { flexGrow = number(1.0) }
            component = ReactHTML.div
            variant = "h6"

            if (props.selected.isNotEmpty()) {
                +"${props.selected.size} selected"
            } else {
                +props.title
            }
        }

        props.actions.forEach { action ->
            if (!action.requiresSelection || props.selected.isNotEmpty()) {
                Tooltip {
                    title = ReactNode(action.name)

                    IconButton {
                        action.iconColor?.let {
                            color = it
                        }
                        onClick = { _ -> action.onClick(props.selected) }

                        action.icon()
                    }
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
    var sortable: Boolean
    var selectable: Boolean
    var onRequestSort: (String) -> Unit
    var onSelectAllClick: (Boolean) -> Unit
}

val EnhancedTableHead = FC<EnhancedTableHeadProps> { props ->
    TableHead {
        TableRow {
            if (props.selectable) {
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

                        if (props.sortable) {
                            sortDirection = if (orderedColumn) props.order else SortDirection.`false`

                            TableSortLabel {
                                onClick = { _ -> props.onRequestSort(column.id) }
                                if (orderedColumn) {
                                    active = true
                                    direction = props.order.toTableSortLabel()
                                }
                                +column.label
                            }
                        } else {
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
    var rows: List<TableRow>
    var columns: List<TableColumn>
    var order: SortDirection
    var orderBy: String
    var selectable: Boolean
    var onRowClick: (String) -> Unit
    var actions: List<RowAction>
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
                    tabIndex = -1

                    if (props.selectable) {
                        role = AriaRole.checkbox
                        ariaChecked = if (isRowSelected) AriaChecked.`true` else AriaChecked.`false`
                        onClick = { _ -> props.onRowClick(row.id) }

                        TableCell {
                            padding = TableCellPadding.checkbox

                            Checkbox {
                                color = CheckboxColor.primary
                                checked = isRowSelected
                                ariaLabelledBy = "enhanced-table-checkbox-${row.id}"
                            }
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

                    if (props.actions.isNotEmpty()) {
                        TableCell {
                            padding = TableCellPadding.normal
                            align = TableCellAlign.center
                            size = Size.small

                            Box {
                                component = ReactHTML.div
                                sx {
                                    display = Display.flex
                                }

                                props.actions.forEach { action ->
                                    Tooltip {
                                        title = ReactNode(action.name)
                                        IconButton {
                                            action.iconColor?.let {
                                                color = it
                                            }
                                            size = action.size
                                            onClick = { event ->
                                                event.stopPropagation()
                                                action.onClick(row.id)
                                            }

                                            action.icon()
                                        }
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
    val name: String,
    val size: Size = Size.small,
    val icon: SvgIconComponent,
    val iconColor: IconButtonColor? = null,
    val onClick: (String) -> Unit,
)

data class ToolbarAction(
    val name: String,
    val size: Size = Size.small,
    val icon: SvgIconComponent,
    val iconColor: IconButtonColor? = null,
    val requiresSelection: Boolean = false,
    val onClick: (List<String>) -> Unit,
)

private fun comparator(order: SortDirection, orderBy: String) = Comparator { a: TableRow, b: TableRow ->
    val aColumn = getSortColumn(a, orderBy)
    val bColumn = getSortColumn(b, orderBy)

    val res = when {
        aColumn < bColumn -> -1
        aColumn > bColumn -> 1
        else -> 0
    }

    if (order == SortDirection.asc) res else -res
}

private fun getSortColumn(row: TableRow, column: String): dynamic {
    val sortColumn = row.asDynamic()["${column}Sort"]
    return if (sortColumn != undefined) {
        sortColumn
    } else {
        row.asDynamic()[column]
    }
}

private fun SortDirection.toTableSortLabel(): TableSortLabelDirection =
    when (this) {
        SortDirection.desc -> TableSortLabelDirection.desc
        else -> TableSortLabelDirection.asc
    }