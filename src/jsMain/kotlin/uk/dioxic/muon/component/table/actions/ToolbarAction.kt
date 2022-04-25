package uk.dioxic.muon.component.table.actions

import mui.icons.material.SvgIconComponent
import mui.material.IconButtonColor

data class ToolbarAction<T: Any>(
    val name: String,
    val icon: SvgIconComponent,
    val iconColor: IconButtonColor? = null,
    val requiresSelection: Boolean = false,
    val onClick: (List<T>) -> Unit,
    val fetchingAnimation: Boolean = false,
)