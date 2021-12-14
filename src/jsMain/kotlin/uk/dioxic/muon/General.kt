package uk.dioxic.muon

import uk.dioxic.muon.audio.ImportError

fun List<ImportError>.alertMessage() = this.joinToString(separator = ", ") { "${it.filename} - ${it.reason}" }