package uk.dioxic.muon.common

import tanstack.query.core.QueryKey


object QueryKeys {
    fun trackSearch(text: String) = QueryKey<QueryKey>("library", text)
    val LIBRARY = QueryKey<QueryKey>("library")
    val SETTINGS = QueryKey<QueryKey>("settings")
    val IMPORT = QueryKey<QueryKey>("import")
    val USERS_QUERY_KEY = QueryKey<QueryKey>("users")
}