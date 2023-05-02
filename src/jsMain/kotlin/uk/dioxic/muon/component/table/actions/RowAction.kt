package uk.dioxic.muon.component.table.actions

import mui.icons.material.SvgIconComponent
import mui.material.IconButtonColor

data class RowAction<T : Any>(
    val name: String,
    val icon: SvgIconComponent? = null,
    val iconFn: ((T) -> SvgIconComponent)? = null,
    val iconColor: IconButtonColor? = null,
    val iconColorFn: ((T) -> IconButtonColor)? = null,
    val onClick: (T) -> Unit,
)
