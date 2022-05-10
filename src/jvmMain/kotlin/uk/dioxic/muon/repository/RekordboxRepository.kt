package uk.dioxic.muon.repository

import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDateTime
import org.apache.logging.log4j.LogManager
import uk.dioxic.muon.exceptions.IdNotFoundException
import uk.dioxic.muon.model.FileType
import uk.dioxic.muon.model.RbColor
import uk.dioxic.muon.model.Track
import java.io.Closeable
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.pathString


class RekordboxRepository(settingsRepository: SettingsRepository) : Closeable {
    private val logger = LogManager.getLogger()
    private val cipherKey = "402fd482c38817c35ffa8ffb8c7d93143b749e7d315df7a81732a1ff43608497"
    private var conn: Connection? = null

    init {
        connect(settingsRepository.get().rekordboxDatabase)
        settingsRepository.addListener { settings ->
            connect(settings.rekordboxDatabase)
        }
    }

    private fun connect(database: String?) {
        close()
        conn = if (database != null) {
            logger.debug("Connecting to database $database")
            DriverManager.getConnection("jdbc:sqlite:$database?cipher=sqlcipher&legacy=4&key=$cipherKey")
        } else {
            null
        }
    }

    private fun getConn(): Connection {
        conn.let {
            requireNotNull(it) { "rekordbox database not set!" }
            return it
        }
    }

    /**
     * Returns all tracks imported later than the input date (if specified)
     */
    fun getTracks(oldestImportDate: LocalDateTime? = null) =
        getConn().createStatement().use { stmt ->
            flow {
                val where = oldestImportDate?.let {
                    "TRACK.created_at > '${oldestImportDate}'"
                }

                val rs = stmt.executeAndLogQuery(where)

                while (rs.next()) {
                    emit(rs.toTrack())
                }
            }
        }

    fun getTrackById(id: String): Track {
        getConn().createStatement().use { stmt ->
            val rs = stmt.executeAndLogQuery("TRACK.ID = $id")
            if (rs.next()) {
                return rs.toTrack()
            } else {
                throw IdNotFoundException(id)
            }
        }
    }

    fun getTracksById(ids: List<String>) =
        getConn().createStatement().use { stmt ->
            flow {
                ids.forEach { id ->
                    val rs = stmt.executeAndLogQuery("TRACK.ID = $id")
                    if (rs.next()) {
                        emit(rs.toTrack())
                    }
                }
            }
        }

    override fun close() {
        conn?.let {
            if (!it.isClosed) {
                it.close()
            }
        }
    }

    private fun Statement.executeAndLogQuery(where: String?): ResultSet {
        val sql = query(where)
        logger.trace(sql)
        return executeQuery(sql)
    }

    private fun ResultSet.toTrack(): Track {
        val fullPath = Path.of(getString("fullPath"))
        return Track(
            title = getString("title").orEmpty(),
            artist = getString("artist").orEmpty(),
            genre = getString("genre").orEmpty(),
            bitrate = getInt("bitRate"),
            length = getInt("length"),
            lyricist = getString("lyricist").orEmpty(),
            path = fullPath.pathString,
            filename = fullPath.nameWithoutExtension,
            album = getString("album").orEmpty(),
            comment = getString("comment").orEmpty(),
            year = getInt("releaseYear").let {
                when (it) {
                    0 -> ""
                    else -> it.toString()
                }
            },
            id = getString("id"),
            type = getInt("fileType").toFileType(),
            fileSize = getInt("fileSize"),
            bpm = getInt("bpm") / 100,
            key = getString("key"),
            color = getString("color")?.let { RbColor.valueOf(it.uppercase()) },
            rating = getInt("rating"),
            tags = getString("tags")?.split(",") ?: emptyList(),
        )
    }

    private fun Int.toFileType() = when (this) {
        5 -> FileType.FLAC
        1 -> FileType.MP3
        11 -> FileType.WAV
        else -> FileType.UNKNOWN
    }

    private fun query(where: String? = null) = """
                    SELECT
                        TRACK.ID as id,
                        TRACK.FolderPath as fullPath,
                        ARTIST.Name as artist,
                        GENRE.Name as genre,
                        ALBUM.Name as album,
                        TRACK.Title as title,
                        TRACK.BitRate as bitRate,
                        TRACK.Length as length,
                        TRACK.Lyricist as lyricist,
                        TRACK.Commnt as comment,
                        TRACK.ReleaseYear as releaseYear,
                        TRACK.FileType as fileType,
                        TRACK.FileSize as fileSize,
						TRACK.Rating as rating,
						KEY.ScaleName as key,
						COLOR.Commnt as color,
                        TRACK.BPM as bpm,
						GROUP_CONCAT(MYTAG.Name) as tags,
                        TRACK.created_at as createdOn,
                        TRACK.updated_at as updatedOn
                    FROM djmdContent as TRACK
                    LEFT JOIN djmdArtist as ARTIST ON TRACK.ArtistID = ARTIST.ID
                    LEFT JOIN djmdAlbum as ALBUM ON TRACK.AlbumID = ALBUM.ID
                    LEFT JOIN djmdGenre as GENRE ON TRACK.GenreID = GENRE.ID
					LEFT JOIN djmdKey as KEY ON TRACK.KeyID = KEY.ID
					LEFT JOIN djmdColor as COLOR ON TRACK.ColorID = COLOR.ID
					LEFT JOIN djmdSongMyTag as SONG_MYTAG ON TRACK.ID = SONG_MYTAG.ContentID
					LEFT JOIN djmdMyTag as MYTAG ON SONG_MYTAG.MyTagID = MYTAG.ID
                    ${where?.let { "WHERE $it" } ?: ""}
					GROUP BY TRACK.ID
                """.trimIndent()
}

