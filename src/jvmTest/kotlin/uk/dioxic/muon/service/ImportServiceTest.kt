package uk.dioxic.muon.service

import com.appmattus.kotlinfixture.kotlinFixture
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import uk.dioxic.muon.config.Settings
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.repository.SettingsRepository
import java.nio.file.Files
import kotlin.io.path.absolutePathString
import kotlin.test.Test

class ImportServiceTest {

    private val settingsRepository = mockk<SettingsRepository>()
    private val fixture = kotlinFixture()

    private fun setupService(settings: Settings): ImportService {
        every { settingsRepository.get() } returns settings
        return ImportService(settingsRepository)
    }

    @Test
    fun `hard delete`() {
        val service = setupService(
            Settings.DEFAULT.copy(
                softDelete = false
            )
        )

        // create file
        val f = Files.createTempFile("track_", ".mp3")

        val track = fixture<Track>().copy(
            path = f.absolutePathString()
        )

        // check file has been created
        assertThat(f).exists()

        service.deleteTrack(track)

        // check file has been deleted
        assertThat(f).doesNotExist()
    }

    @Test
    fun `soft delete`() {
        val tmpDir = Files.createTempDirectory("delete_")
        val service = setupService(
            Settings.DEFAULT.copy(
                softDelete = true,
                deletePath = tmpDir.absolutePathString()
            )
        )

        // create file
        val f = Files.createTempFile("track_", ".mp3")

        val track = fixture<Track>().copy(
            path = f.absolutePathString()
        )

        // check file has been created
        assertThat(f).exists()

        service.deleteTrack(track)

        // check file has been deleted
        assertThat(f).doesNotExist()

        // check file has been moved to the soft delete dir
        assertThat(tmpDir.resolve(f.fileName)).exists()
    }

}