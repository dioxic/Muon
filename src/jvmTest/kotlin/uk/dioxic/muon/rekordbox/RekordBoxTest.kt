package uk.dioxic.muon.rekordbox

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import uk.dioxic.muon.model.rekordbox.Artists
import uk.dioxic.muon.model.rekordbox.Genres
import uk.dioxic.muon.model.rekordbox.Tracks
import uk.dioxic.muon.model.rekordbox.Tracks.genreId
import java.time.format.DateTimeFormatter
import kotlin.test.Test

class RekordBoxTest {
    private val rekordboxDb = "J:\\rekordbox\\master.backup.db"
    private val cipherKey = "402fd482c38817c35ffa8ffb8c7d93143b749e7d315df7a81732a1ff43608497"

    @Test
    fun countTracks() {
        Database.connect("jdbc:sqlite:$rekordboxDb?cipher=sqlcipher&legacy=4&key=$cipherKey", "org.sqlite.JDBC")

        transaction {
            println("Tracks count: ${Tracks.selectAll().count()}")
        }
    }

    @Test
    fun getSomeTracks() {
        Database.connect("jdbc:sqlite:$rekordboxDb?cipher=sqlcipher&legacy=4&key=$cipherKey", "org.sqlite.JDBC")

        transaction {
            Tracks.selectAll().limit(10).forEach {
                println("title = ${it[Tracks.title]}")
            }
        }
    }

    @Test
    fun joinWithArtist() {
        Database.connect("jdbc:sqlite:$rekordboxDb?cipher=sqlcipher&legacy=4&key=$cipherKey", "org.sqlite.JDBC")

        transaction {
            Tracks.innerJoin(Artists, { artistId }, { id })
                .innerJoin(Genres, { genreId }, { id })
//                .slice(Tracks.title, Artists.name)
                .selectAll()
//                .select { Tracks.artistId eq Artists.id }
                .limit(10)
                .forEach {
                    println(
                        """
                        id=${it[Tracks.id]}, 
                        artist="${it[Artists.name]}", 
                        title="${it[Tracks.title]}", 
                        genre="${it[Genres.name]}"
                        updatedOn="${it[Artists.updatedOn]}"
                        """.trimIndent().replace("\n", "")
                    )
                }
        }
    }

    @Test
    fun dateTest() {
        val dateStr = "2021-03-24 06:02:26.846 +00:00"

        val pattern = DateTimeFormatter.ofPattern("yyyy-MM-d HH:mm:ss")

        println(pattern.parse(dateStr))
    }

}