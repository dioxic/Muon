package uk.dioxic.muon.repository

import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDateTime
import uk.dioxic.muon.model.Track
import java.nio.file.Path
import java.sql.DriverManager
import kotlin.io.path.name

private const val cipherKey = "402fd482c38817c35ffa8ffb8c7d93143b749e7d315df7a81732a1ff43608497"

/**
 * Returns all tracks imported later than the input date (if specified)
 */
fun getRekordboxTracks(rbDatabase: Path, oldestImportDate: LocalDateTime? = null) = flow {

    val url = "jdbc:sqlite:${rbDatabase}?cipher=sqlcipher&legacy=4&key=$cipherKey"

    DriverManager.getConnection(url).use { conn ->
        conn.createStatement().use { stmt ->
            var query = """
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
                        track.created_at as createdOn,
                        track.updated_at as updatedOn
                    FROM djmdContent as track
                    LEFT JOIN djmdArtist as artist ON track.ArtistID = artist.ID
                    LEFT JOIN djmdAlbum as album ON track.AlbumID = album.ID
                    LEFT JOIN djmdGenre as genre ON track.GenreID = genre.ID
                """.trimIndent()

            oldestImportDate?.let {
                query += "\nWHERE track.created_at > '${oldestImportDate}'"
            }

            val rs = stmt.executeQuery(query)

            while (rs.next()) {
                val fullPath = Path.of(rs.getString("fullPath"))
                emit(
                    Track(
                        title = rs.getString("title").orEmpty(),
                        artist = rs.getString("artist").orEmpty(),
                        genre = rs.getString("genre").orEmpty(),
                        bitrate = rs.getInt("bitRate"),
                        length = rs.getInt("length"),
                        lyricist = rs.getString("lyricist").orEmpty(),
                        path = fullPath.parent.name,
                        filename = fullPath.fileName.name,
                        album = rs.getString("album").orEmpty(),
                        comment = rs.getString("comment").orEmpty(),
                        year = rs.getInt("releaseYear"),
                        id = rs.getString("id")
                    )
                )
            }
        }
    }
}
