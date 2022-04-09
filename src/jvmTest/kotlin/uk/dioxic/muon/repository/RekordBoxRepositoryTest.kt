package uk.dioxic.muon.repository

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import uk.dioxic.muon.config.Settings
import kotlin.test.Test

@ExperimentalCoroutinesApi
class RekordBoxRepositoryTest {

    private val settings = Settings.DEFAULT.copy(
        rekordboxDatabase = RekordBoxRepositoryTest::class.java.getResource("/rekordbox.db")?.file
    )

    private val settingsRepository = mockk<SettingsRepository> {
        every { get() } returns settings
    }
    private val rekordboxRepository = RekordboxRepository(settingsRepository)

    @Test
    @DisplayName("Track count")
    fun getTrackCount() {
        every { settingsRepository.get() } returns settings

        runTest {
            val count = rekordboxRepository.getTracks()
                .count()
            assertThat(count).isEqualTo(65)
        }
    }

    @Test
    @DisplayName("Tracks filter by updateDate")
    fun takeSomeTracks() {
        runTest {
            val count = rekordboxRepository
                .getTracks(LocalDateTime(2022, 4, 9, 0, 0, 0))
                .count()

            assertThat(count).isEqualTo(3)
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