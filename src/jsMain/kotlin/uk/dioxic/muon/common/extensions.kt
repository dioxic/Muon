package uk.dioxic.muon.common

import mui.material.SortDirection
import mui.material.TableSortLabelDirection

fun SortDirection.toTableSortLabel(): TableSortLabelDirection =
    when (this) {
        SortDirection.desc -> TableSortLabelDirection.desc
        else -> TableSortLabelDirection.asc
    }

fun SortDirection.toText(): String =
    when (this) {
        SortDirection.desc -> "sorted descending"
        else -> "sorted ascending"
    }

