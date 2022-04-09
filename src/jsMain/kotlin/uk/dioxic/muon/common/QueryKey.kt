package uk.dioxic.muon.common

import react.query.QueryKey

// TODO QueryKey changes in pre.329 - pretty sure this isn't how it's supposed to work!

object QueryKey {
    fun importTrack(id: String) = QueryKey<QueryKey>("settings", id)
    val SETTINGS = QueryKey<QueryKey>("settings")
    val IMPORT = QueryKey<QueryKey>("import")
}