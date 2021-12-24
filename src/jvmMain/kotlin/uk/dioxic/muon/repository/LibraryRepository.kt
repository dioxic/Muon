package uk.dioxic.muon.repository

import uk.dioxic.muon.model.Library

interface LibraryRepository {

    fun getLibraryById(libraryId: String): Library

    fun getLibraryByPath(path: String): Library

    fun getLibraries(): List<Library>

    fun saveLibrary(library: Library)

    fun deleteLibrary(libraryId: String)

}