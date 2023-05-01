package uk.dioxic.muon.component.table.actions

import mui.icons.material.SvgIconComponent
import mui.material.IconButtonColor

data class ToolbarAction(
    val name: String,
    val icon: SvgIconComponent,
    val iconColor: IconButtonColor? = null,
    val visible: Boolean = true,
    val onClick: () -> Unit,
    val fetchingAnimation: Boolean = false,
)