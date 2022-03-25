package uk.dioxic.muon.model.rekordbox

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object Tracks: Table("djmdContent") {
    val id = varchar("ID", 255)
    val folderPath = varchar("FolderPath", 255)
    val title = varchar("Title", 255)
    val artistId = varchar("ArtistID", 255)
    val albumId = varchar("AlbumID", 255)
    val genreId = varchar("GenreID", 255)
    val bitRate = integer("BitRate")
    val length = integer("Length")
    val lyricist = varchar("Lyricist", 255)
    val createdOn = datetime("created_at")
    val updatedOn = datetime("updated_at")

//    override val primaryKey = PrimaryKey(id)
}