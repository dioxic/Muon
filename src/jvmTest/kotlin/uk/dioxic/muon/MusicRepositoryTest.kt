package uk.dioxic.muon

import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class MusicRepositoryTest {}
//
//    @Test
//    fun testProperty() {
//        assertEquals(System.getProperty("java.util.logging.manager"), "org.apache.logging.log4j.jul.LogManager")
//    }
//
//    @Test
//    @ExperimentalTime
//    @FlowPreview
//    fun loadLibraryTest(@MockK libraryRepo: LibraryRepository) {
//        val library = createLibrary(
//            name = "myLibrary",
////            path = "J:\\import\\complete",
//            path = "J:\\music\\dubstep",
//        )
//
//        every { libraryRepo.getLibraryById(any()) } returns library
//
//        val musicRepo = MusicRepositoryImpl("electronica")
//
//        runBlocking {
//            musicRepo.refreshIndex(library)
////            delay(Duration.Companion.seconds(10))
//        }
//
//        val indexSize1 = musicRepo.size()
//
//        val filesIndexed = runBlocking {
//            musicRepo.refreshIndex(library)
//        }
//
//        val indexSize2 = musicRepo.size()
//
//        assertEquals(indexSize1, indexSize2, "index size")
//        assertEquals(0, filesIndexed, "index count")
//
//        musicRepo.search(
//            query = MatchAllDocsQuery(),
//            maxResults = 10,
//            sortField = "title"
//        ).forEach {
//            println("${it.docId} - ${it.audioFile.tags.title}")
//        }
//
//        val indexLocation = musicRepo.indexLocation
//
////        musicRepo.dropIndex()
//        musicRepo.close()
////        musicRepo.close()
//
//        File(indexLocation.toUri()).deleteRecursively()
//
////        Files.walk(indexLocation)
////            .forEach { Files.delete(it) }
////
////        Files.delete(indexLocation)
//
//    }
//
//}