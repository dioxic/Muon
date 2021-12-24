package uk.dioxic.muon

import kotlin.test.Test
import kotlin.test.assertEquals

class LocationTest {

    @Test
    fun testExtensionDefault() {
        checkExtension("something.mp3", "mp3")
        checkExtension("something.flac", "flac")
        checkExtension("path/other/something.mp3", "mp3")
        checkExtension("C:\\path\\other\\something.mp3", "mp3")
    }

    private fun checkExtension(input: String, expected: String) {
        assertEquals(input.fileExtension(), expected)
    }
}