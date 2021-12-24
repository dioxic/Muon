package uk.dioxic.muon

import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.audio.Header
import uk.dioxic.muon.audio.Location
import uk.dioxic.muon.audio.Tags
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ExtensionsTest {

    @Test
    fun convertAudioFiletoDocument() {
        val audioFile = AudioFile(
            id = UUID.randomUUID().toString(),
            tags = Tags(
                artist = "Bob",
                title = "song",
                lyricist = "MC Bob",
                year = "2001",
                genre = "Bass",
                comment = "comment",
                album = "album"
            ),
            location = Location(),
            header = Header()
        )

        assertEquals(audioFile, audioFile.toDocument("myLibrary").toAudioFile())
    }

}