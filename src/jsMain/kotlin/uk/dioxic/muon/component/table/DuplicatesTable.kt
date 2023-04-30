package uk.dioxic.muon.component.table

import js.core.jso
import mui.icons.material.CheckCircle
import mui.icons.material.Delete
import mui.material.*
import react.FC
import react.Props
import react.ReactNode
import tanstack.react.query.useQueryClient
import uk.dioxic.muon.common.QueryKeys
import uk.dioxic.muon.hook.useTrackDelete
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.model.Tracks
import kotlin.time.Duration.Companion.seconds

external interface DuplicatesProps : Props {
    var track: Track
}

val DuplicatesTable = FC<DuplicatesProps> { props ->
    val queryClient = useQueryClient()
    val delete = useTrackDelete()

    fun handleDeleteClick(duplicate: Track) {
        delete(duplicate)
    }

    fun handleIgnoreClick(duplicate: Track) {
        queryClient.cancelQueries(QueryKeys.IMPORT)
        queryClient.setQueryData<Tracks>(
            queryKey = QueryKeys.IMPORT,
            updater = {
                it?.map { track ->
                    if (track.id == props.track.id) {
                        track.copy(duplicates = track.duplicates?.filterNot { dup -> dup.id == duplicate.id })
                    } else {
                        track
                    }
                } ?: emptyList()
            },
            options = jso()
        )
    }

    Table {
        size = Size.small
        TableHead {
            TableRow {
                TableCell { +"Title" }
                TableCell { +"Artist" }
                TableCell { +"Lyricist" }
                TableCell { +"Location" }
                TableCell { +"Bitrate" }
                TableCell { +"Length" }
                TableCell { +"Type" }
                TableCell { }
            }
        }
        TableBody {
            props.track.duplicates?.forEach { track ->
                TableRow {
                    TableCell { +track.title }
                    TableCell { +track.artist }
                    TableCell { +track.lyricist }
                    TableCell { +track.path }
                    TableCell { +track.bitrate.toString() }
                    TableCell { +track.length.seconds.toString() }
                    TableCell { +track.type.toString() }
                    TableCell {
                        align = TableCellAlign.right
                        Tooltip {
                            title = ReactNode("not a duplicate")
                            IconButton {
                                color = IconButtonColor.success
                                size = Size.small
                                onClick = { event ->
                                    event.stopPropagation()
                                    handleIgnoreClick(track)
                                }

                                CheckCircle()
                            }
                        }
                        Tooltip {
                            title = ReactNode("delete")
                            IconButton {
                                color = IconButtonColor.error
                                size = Size.small
                                onClick = { event ->
                                    event.stopPropagation()
                                    handleDeleteClick(track)
                                }

                                Delete()
                            }
                        }
                    }
                }
            }
        }
    }
}