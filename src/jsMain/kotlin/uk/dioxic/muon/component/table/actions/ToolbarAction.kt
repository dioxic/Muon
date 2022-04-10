package uk.dioxic.muon.component.table.actions

import kotlinx.js.ReadonlyArray
import mui.icons.material.SvgIconComponent
import mui.material.IconButtonColor
import react.table.Row

data class ToolbarAction<T: Any>(
    val name: String,
    val icon: SvgIconComponent,
    val iconColor: IconButtonColor? = null,
    val requiresSelection: Boolean = false,
    val onClick: (ReadonlyArray<Row<T>>) -> Unit,
    val fetchingAnimation: Boolean = false,
)