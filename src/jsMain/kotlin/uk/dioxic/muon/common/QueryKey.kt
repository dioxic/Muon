package uk.dioxic.muon.common

import react.query.QueryKey

object QueryKey {
    fun trackSearch(text: String) = QueryKey<QueryKey>("track", text)
    val SETTINGS = QueryKey<QueryKey>("settings")
    val IMPORT = QueryKey<QueryKey>("import")
}