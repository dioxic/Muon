package uk.dioxic.muon.service

import com.appmattus.kotlinfixture.kotlinFixture
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
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

        // check file has been created
        assertThat(f).exists()

        service.deleteTrack(
            fixture<Track>().copy(
                path = f.absolutePathString()
            )
        )

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

        // check file has been created
        assertThat(f).exists()

        service.deleteTrack(
            fixture<Track>().copy(
                path = f.absolutePathString()
            )
        )

        // check file has been moved
        assertThat(f).doesNotExist()
        assertThat(tmpDir.resolve(f.fileName)).exists()
    }

    @Test
    fun `import track`() {
        val tmpDir = Files.createTempDirectory("import_")
        val service = setupService(
            Settings.DEFAULT.copy(
                importPath = tmpDir.absolutePathString()
            )
        )

        // create file
        val f = Files.createTempFile("track_", ".mp3")

        // check file has been created
        assertThat(f).exists()

        service.importTrack(
            fixture<Track>().copy(
                path = f.absolutePathString()
            )
        )

        // check file has been moved
        assertThat(f).doesNotExist()
        assertThat(tmpDir.resolve(f.fileName)).exists()
    }

    @Test
    fun `import track fails when file already exists`() {
        val tmpDir = Files.createTempDirectory("import_")
        val service = setupService(
            Settings.DEFAULT.copy(
                importPath = tmpDir.absolutePathString()
            )
        )

        // create file
        val f = Files.createTempFile("track_", ".mp3")

        // create existing
        val existing = Files.createFile(tmpDir.resolve(f.fileName))

        // check files have been created
        assertThat(f).exists()
        assertThat(existing).exists()

        val exception = assertThrows<java.nio.file.FileAlreadyExistsException> {
            service.importTrack(
                fixture<Track>().copy(
                    path = f.absolutePathString()
                )
            )
        }

        // check file hasn't been deleted
        assertThat(f).exists()
    }

}