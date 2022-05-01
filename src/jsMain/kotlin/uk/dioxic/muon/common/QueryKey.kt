package uk.dioxic.muon.common

import react.query.QueryKey

object QueryKey {
    fun trackSearch(text: String) = QueryKey<QueryKey>("library", text)
    val LIBRARY = QueryKey<QueryKey>("library")
    val SETTINGS = QueryKey<QueryKey>("settings")
    val IMPORT = QueryKey<QueryKey>("import")
}