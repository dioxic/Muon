package uk.dioxic.muon.rekordbox

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import uk.dioxic.muon.repository.RekordboxRepository
import java.nio.file.Paths
import kotlin.test.Test

@ExperimentalCoroutinesApi
class RekordBoxRepositoryTest {

    private val rekordboxRepository =RekordboxRepository(Paths.get("J:\\rekordbox\\master.backup3.db"))

    @Test
    @DisplayName("Track count")
    fun getTrackCount() {
        runTest {
            val count = rekordboxRepository.getRekordboxTracks()
                .count()

            println(count)
        }
    }

    @Test
    @DisplayName("Tracks filter by updateDate")
    fun takeSomeTracks() {
        runTest {
            rekordboxRepository.getRekordboxTracks(LocalDateTime(2021, 1, 1, 0, 0, 0))
                .take(20)
                .collect {
                    println(it)
                }
        }
    }

    @Test
    @DisplayName("Tracks by Id")
    fun getTracksById() {
        val ids = arrayOf("104605110", "127296874")
        runTest {
            val actual = rekordboxRepository.getRekordboxTracksById(ids.toList())
                .onEach { println(it) }
                .map { track -> track.id }
                .toList(mutableListOf())

            assertThat(actual)
                .containsExactly(*ids)

        }
    }

}