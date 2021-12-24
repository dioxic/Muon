package uk.dioxic.muon

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.extension.ExtendWith
import uk.dioxic.muon.repository.LibraryRepository
import uk.dioxic.muon.repository.MusicRepositoryImpl
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class MusicRepositoryTest {

    @Test
    fun testProperty() {
        assertEquals(System.getProperty("java.util.logging.manager"), "org.apache.logging.log4j.jul.LogManager")
    }

    @Test
//    @ExperimentalTime
    fun loadLibraryTest(@MockK libraryRepo: LibraryRepository) {
        val library = createLibrary(
            name = "myLibrary",
//            path = "J:\\import\\complete",
            path = "J:\\music\\House",
        )

        every { libraryRepo.getLibraryById(any()) } returns library

        val musicRepo = MusicRepositoryImpl("testIndex")

        runBlocking {
            musicRepo.refreshIndex(library)
//            delay(Duration.Companion.seconds(10))
        }

        val indexSize1 = musicRepo.size()

        val filesIndexed = runBlocking {
            musicRepo.refreshIndex(library)
        }

        val indexSize2 = musicRepo.size()

        assertEquals(indexSize1, indexSize2, "index size")
        assertEquals(0, filesIndexed, "index count")

        musicRepo.search(text = "dimension", maxResults = 10).forEach {
            println(it.location)
        }

        val indexLocation = musicRepo.indexLocation

//        musicRepo.dropIndex()
        musicRepo.close()
//        musicRepo.close()

        File(indexLocation.toUri()).deleteRecursively()

//        Files.walk(indexLocation)
//            .forEach { Files.delete(it) }
//
//        Files.delete(indexLocation)

    }

}