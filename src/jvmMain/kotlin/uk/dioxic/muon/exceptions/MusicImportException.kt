package uk.dioxic.muon.exceptions

import uk.dioxic.muon.audio.ImportError

class MusicImportException(val errors: List<ImportError>) : Exception()