package uk.dioxic.muon.component.table

import mui.icons.material.SvgIconComponent
import mui.material.IconButtonColor
import mui.material.Size
import react.table.Row

data class RowAction<T : Any>(
    val name: String,
    val icon: SvgIconComponent,
    val iconColor: IconButtonColor? = null,
    val size: Size = Size.small,
    val onClick: (Row<T>) -> Unit,
)
