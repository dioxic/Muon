package uk.dioxic.muon.common

import web.cssom.Length

inline val Number.percent: Length
    get() = "${this}%".unsafeCast<Length>()