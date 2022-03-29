package uk.dioxic.muon.component.page

import mui.icons.material.Delete
import mui.icons.material.Edit
import mui.icons.material.GetApp
import mui.icons.material.Refresh
import react.FC
import react.Props
import react.useContext
import react.useEffectOnce
import uk.dioxic.muon.component.EnhancedTable
import uk.dioxic.muon.component.RowAction
import uk.dioxic.muon.component.ToolbarAction
import uk.dioxic.muon.context.AppContext
import uk.dioxic.muon.model.toColumns
import uk.dioxic.muon.model.toRows

val ImportPage = FC<Props> {
    val ac = useContext(AppContext)
    val columns = ac.settings.importTableColumns
    val tracks = ac.importTracks

    useEffectOnce {
        val job = ac.loadImportTracks()
        cleanup {
            job.cancel()
        }
    }

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

    fun handleRefreshClick(selected: List<String>) {
        println("handleRefresh")
        ac.loadImportTracks()
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
        this.rows = tracks.toRows()
        this.columns = columns.toColumns()
        this.selectable = true
        this.sortable = true
    }
}