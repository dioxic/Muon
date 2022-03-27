package uk.dioxic.muon.exceptions

import uk.dioxic.muon.model.ImportError

class MusicImportException(val errors: List<ImportError>) : Exception()