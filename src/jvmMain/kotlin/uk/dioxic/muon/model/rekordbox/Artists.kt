package uk.dioxic.muon.model.rekordbox

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object Artists: Table("djmdArtist") {
    val id = varchar("ID", 255)
    val name = varchar("Name", 255)
    val createdOn = datetime("created_at")
    val updatedOn = datetime("updated_at")

//    override val primaryKey = PrimaryKey(Tracks.id)
}