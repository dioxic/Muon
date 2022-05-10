package uk.dioxic.muon.common

import csstype.Length

inline val Number.percent: Length
    get() = "${this}%".unsafeCast<Length>()