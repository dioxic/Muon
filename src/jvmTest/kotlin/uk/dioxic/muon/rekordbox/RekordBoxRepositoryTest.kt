package uk.dioxic.muon.rekordbox

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import uk.dioxic.muon.common.Global
import uk.dioxic.muon.repository.getRekordboxTracks
import java.nio.file.Paths
import kotlin.test.Test

class RekordBoxRepositoryTest {

    private val rbDatabase = Paths.get("J:\\rekordbox\\master.db")

    @Test
    fun getTrackCount() {
        runBlocking {
            val count = getRekordboxTracks(rbDatabase)
                .count()

            println(count)
        }
    }

    @Test
    fun takeSomeTracks() {

        runBlocking {
            getRekordboxTracks(rbDatabase, LocalDateTime(2021, 1, 1, 0, 0, 0))
                .take(20)
                .collect {
                    println(it)
                }
        }
    }

}