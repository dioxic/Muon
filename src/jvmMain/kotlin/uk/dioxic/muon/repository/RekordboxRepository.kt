package uk.dioxic.muon.repository

import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDateTime
import org.apache.logging.log4j.LogManager
import uk.dioxic.muon.exceptions.IdNotFoundException
import uk.dioxic.muon.model.FileType
import uk.dioxic.muon.model.Track
import java.io.Closeable
import java.nio.file.Path
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import kotlin.io.path.name


class RekordboxRepository(settingsRepository: SettingsRepository) : Closeable {
    private val logger = LogManager.getLogger()
    private val cipherKey = "402fd482c38817c35ffa8ffb8c7d93143b749e7d315df7a81732a1ff43608497"
    private val baseQuery = """
                    SELECT
                        track.ID as id,
                        FolderPath as fullPath,
                        artist.Name as artist,
                        genre.Name as genre,
                        album.Name as album,
                        Title as title,
                        BitRate as bitRate,
                        Length as length,
                        Lyricist as lyricist,
                        Commnt as comment,
                        ReleaseYear as releaseYear,
                        FileType as fileType,
                        FileSize as fileSize,
                        track.created_at as createdOn,
                        track.updated_at as updatedOn
                    FROM djmdContent as track
                    LEFT JOIN djmdArtist as artist ON track.ArtistID = artist.ID
                    LEFT JOIN djmdAlbum as album ON track.AlbumID = album.ID
                    LEFT JOIN djmdGenre as genre ON track.GenreID = genre.ID
                """.trimIndent()
    private val url =
        "jdbc:sqlite:${settingsRepository.get().rekordboxDatabase}?cipher=sqlcipher&legacy=4&key=$cipherKey"
    private val conn = DriverManager.getConnection(url)

    /**
     * Returns all tracks imported later than the input date (if specified)
     */
    fun getTracks(oldestImportDate: LocalDateTime? = null) =
        conn.createStatement().use { stmt ->
            flow {
                var query = baseQuery

                oldestImportDate?.let {
                    query += "\nWHERE track.created_at > '${oldestImportDate}'"
                }

                val rs = stmt.executeAndLogQuery(query)

                while (rs.next()) {
                    emit(rs.toTrack())
                }
            }
        }

    fun getTrackById(id: String): Track {
        conn.createStatement().use { stmt ->
            val rs = stmt.executeAndLogQuery("$baseQuery\nWHERE track.id = $id")
            if (rs.next()) {
                return rs.toTrack()
            } else {
                throw IdNotFoundException(id)
            }
        }
    }

    fun getTracksById(ids: List<String>) =
        conn.createStatement().use { stmt ->
            flow {
                ids.forEach {
                    val rs = stmt.executeAndLogQuery("$baseQuery\nWHERE track.id = $it")
                    if (rs.next()) {
                        emit(rs.toTrack())
                    }
                }
            }
        }

    override fun close() {
        conn.close()
    }

    private fun Statement.executeAndLogQuery(sql: String): ResultSet {
        logger.debug(sql)
        return executeQuery(sql)
    }

    private fun ResultSet.toTrack(): Track {
        val fullPath = Path.of(this.getString("fullPath"))
        return Track(
            title = this.getString("title").orEmpty(),
            artist = this.getString("artist").orEmpty(),
            genre = this.getString("genre").orEmpty(),
            bitrate = this.getInt("bitRate"),
            length = this.getInt("length"),
            lyricist = this.getString("lyricist").orEmpty(),
            path = fullPath.parent.name,
            filename = fullPath.fileName.name,
            album = this.getString("album").orEmpty(),
            comment = this.getString("comment").orEmpty(),
            year = this.getInt("releaseYear").let {
                when (it) {
                    0 -> ""
                    else -> it.toString()
                }
            },
            id = this.getString("id"),
            type = this.getInt("fileType").toFileType(),
            fileSize = this.getInt("fileSize")
        )
    }

    private fun Int.toFileType() = when (this) {
        5 -> FileType.FLAC
        1 -> FileType.MP3
        11 -> FileType.WAV
        else -> FileType.UNKNOWN
    }
}

