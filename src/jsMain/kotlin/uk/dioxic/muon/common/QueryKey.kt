package uk.dioxic.muon.common

import react.query.QueryKey

object QueryKey {
    fun importTrack(id: String) = QueryKey<QueryKey>("import", id)
    val SETTINGS = QueryKey<QueryKey>("settings")
    val IMPORT = QueryKey<QueryKey>("import")
}