package uk.dioxic.muon.exceptions

class IdNotFoundException(val id: String): Exception("Audio ID not found!")