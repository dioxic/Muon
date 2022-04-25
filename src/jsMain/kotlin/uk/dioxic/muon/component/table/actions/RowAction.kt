package uk.dioxic.muon.component.table.actions

import mui.icons.material.SvgIconComponent
import mui.material.IconButtonColor

data class RowAction<T : Any>(
    val name: String,
    val icon: SvgIconComponent,
    val iconColor: IconButtonColor? = null,
    val onClick: (T) -> Unit,
)
