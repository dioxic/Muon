package uk.dioxic.muon.common

import tanstack.table.core.RowData
import tanstack.table.core.Table

fun <TData : RowData> Table<TData>.getSelectedData() =
    getSelectedRowModel().rows.map { it.original }.toTypedArray()

fun <TData : RowData> Table<TData>.getIsAnyRowsSelected() =
    getIsAllRowsSelected() || getIsSomeRowsSelected()
