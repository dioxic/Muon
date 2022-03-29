package uk.dioxic.muon.rekordbox

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import uk.dioxic.muon.config.Settings
import uk.dioxic.muon.repository.RekordboxRepository
import uk.dioxic.muon.repository.SettingsRepository
import kotlin.test.Test

@ExperimentalCoroutinesApi
class RekordBoxRepositoryTest {

    private val settingsRepository = mockk<SettingsRepository> {
        every { get() } returns Settings.DEFAULT.copy(
            rekordboxDatabase = "J:\\rekordbox\\master.backup3.db"
        )
    }
    private val rekordboxRepository = RekordboxRepository(settingsRepository)

    @Test
    @DisplayName("Track count")
    fun getTrackCount() {
        every { settingsRepository.get() } returns Settings.DEFAULT.copy(
            rekordboxDatabase = "J:\\rekordbox\\master.backup3.db"
        )
        runTest {
            val count = rekordboxRepository.getTracks()
                .count()
            println(count)
        }
    }

    @Test
    @DisplayName("Tracks filter by updateDate")
    fun takeSomeTracks() {
        runTest {
            rekordboxRepository.getTracks(LocalDateTime(2021, 1, 1, 0, 0, 0))
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
            val actual = rekordboxRepository.getTracksById(ids.toList())
                .onEach { println(it) }
                .map { track -> track.id }
                .toList(mutableListOf())

            assertThat(actual)
                .containsExactly(*ids)

        }
    }

}