package uk.dioxic.muon.common

import mui.material.TableSortLabelDirection
import tanstack.table.core.Column
import tanstack.table.core.RowData
import tanstack.table.core.SortDirection
import tanstack.table.core.Table

fun <TData : RowData> Table<TData>.getSelectedData() =
    getSelectedRowModel().rows.map { it.original }.toTypedArray()

fun <TData : RowData> Table<TData>.getIsAnyRowsSelected() =
    getIsAllRowsSelected() || getIsSomeRowsSelected()

fun <TData : RowData, TValue> Column<TData, TValue>.getTableSortLabelDirection(): TableSortLabelDirection {
    return when (getIsSorted()) {
        SortDirection.asc -> TableSortLabelDirection.asc
        else -> TableSortLabelDirection.desc
    }
}

fun <TData : RowData, TValue> Column<TData, TValue>.getIsSortedBoolean(): Boolean {
    val isSorted = getIsSorted()
    return isSorted == SortDirection.asc || isSorted == SortDirection.desc
}