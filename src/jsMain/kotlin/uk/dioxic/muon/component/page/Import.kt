package uk.dioxic.muon.component.page

import mui.icons.material.Delete
import mui.icons.material.Edit
import mui.icons.material.GetApp
import mui.icons.material.Refresh
import react.FC
import react.Props
import uk.dioxic.muon.component.EnhancedTable
import uk.dioxic.muon.component.RowAction
import uk.dioxic.muon.component.ToolbarAction
import uk.dioxic.muon.hook.useImport
import uk.dioxic.muon.hook.useReloadImport
import uk.dioxic.muon.hook.useSettings
import uk.dioxic.muon.model.toColumns
import uk.dioxic.muon.model.toRows

val ImportPage = FC<Props> {
    val settings = useSettings().data
    val import = useImport()
//    val queryClient = useQueryClient()
    val reloadImport = useReloadImport()

    fun handleEditClick(id: String) {
        println("handleEdit for $id")
    }

    fun handleDeleteClick(id: String) {
        println("handleDelete for $id")
    }

    fun handleDeleteClick(ids: List<String>) {
        println("handleDelete for $ids")
    }

    fun handleImportClick(id: String) {
        println("handleImport for $id")
    }

    fun handleFilterClick(selected: List<String>) {
        println("handleFilter for $selected")
    }

    @Suppress("UNUSED_PARAMETER")
    fun handleRefreshClick(selected: List<String>) {
        println("handleRefresh")
        reloadImport()
    }

    val rowActions = listOf(
        RowAction(name = "edit", icon = Edit, onClick = ::handleEditClick),
        RowAction(name = "import", icon = GetApp, onClick = ::handleImportClick),
        RowAction(name = "delete", icon = Delete, onClick = ::handleDeleteClick),
    )

    val toolbarActions = listOf(
        ToolbarAction(name = "import", icon = GetApp, onClick = ::handleFilterClick, requiresSelection = true),
        ToolbarAction(name = "delete", icon = Delete, onClick = ::handleDeleteClick, requiresSelection = true),
        ToolbarAction(name = "refresh", icon = Refresh, onClick = ::handleRefreshClick),
    )

    EnhancedTable {
        this.title = "Music Import"
        this.rowActions = rowActions
        this.toolbarActions = toolbarActions
        this.rows = import.data.toRows()
        this.columns = settings?.importTableColumns.toColumns()
        this.selectable = true
        this.sortable = true
    }
}