package uk.dioxic.muon.rekordbox

import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import uk.dioxic.muon.repository.getRekordboxTracks
import kotlin.test.Test

class RekordBoxRepositoryTest {

    @Test
    fun getTrackCount() {
        runBlocking {
            val count = getRekordboxTracks()
                .count()

            println(count)
        }

    }

    @Test
    fun takeSomeTracks() {
        runBlocking {
            getRekordboxTracks()
                .take(20)
                .collect {
                    println(it)
                }
        }

    }

}