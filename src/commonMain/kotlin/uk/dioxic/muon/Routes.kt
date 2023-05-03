package uk.dioxic.muon

import uk.dioxic.muon.model.Track

object Routes {
    const val import = "/api/import"
    const val settings = "/api/settings"
    const val index = "/api/index"
    const val track = "/api/track"
    const val static = "/static"

    fun trackApi(track: Track) =
        "${Routes.track}/${track.id}"

    fun trackAudio(track: Track) =
        trackApi(track) + "/audio"
}