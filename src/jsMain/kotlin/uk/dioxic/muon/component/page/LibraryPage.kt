package uk.dioxic.muon.component.page

import js.core.jso
import mui.icons.material.Circle
import mui.icons.material.CircleOutlined
import mui.icons.material.PauseCircle
import mui.icons.material.PlayCircle
import mui.material.*
import mui.material.styles.Theme
import mui.material.styles.useTheme
import mui.system.sx
import react.*
import tanstack.react.table.useReactTable
import tanstack.table.core.ColumnDef
import tanstack.table.core.ColumnDefTemplate
import tanstack.table.core.StringOrTemplateHeader
import tanstack.table.core.getCoreRowModel
import uk.dioxic.muon.component.table.BasicTable
import uk.dioxic.muon.component.table.SearchTableToolbar
import uk.dioxic.muon.component.table.actions.RowAction
import uk.dioxic.muon.component.table.columns.rowActionTemplate
import uk.dioxic.muon.context.IsPlayingContext
import uk.dioxic.muon.context.PlayTrackContext
import uk.dioxic.muon.context.TogglePlayStateContext
import uk.dioxic.muon.hook.useTrackSearch
import uk.dioxic.muon.model.Track
import web.cssom.Color
import kotlin.time.Duration.Companion.seconds

private val minimumWidths = mapOf(
    "title" to 120,
    "artist" to 120,
    "genre" to 115,
    "album" to 120,
    "lyricist" to 120,
    "comment" to 120,
    "bitrate" to 101,
    "year" to 87,
    "color" to 93,
    "bpm" to 89,
    "rating" to 120,
    "tags" to 90,
    "length" to 103,
    "type" to 89,
)

val LibraryPage = VFC {
    val theme = useTheme<Theme>()
    val (searchText, setSearchText) = useState("")
    val search = useTrackSearch(searchText)
    val tracks = search.data ?: emptyArray()
    val isPlaying = useContext(IsPlayingContext)!!
    val (playTrack, setPlayTrack) = useContext(PlayTrackContext)!!
    val togglePlayState = useContext(TogglePlayStateContext)!!

    fun handlePlayClick(track: Track) {
        if (track != playTrack) {
            setPlayTrack(track)
        }
        else {
            togglePlayState()
        }
    }

    val rowActions = listOf(
        RowAction(
            name = "Play",
            iconFn = { track ->
                if (isPlaying && playTrack == track) {
                    PauseCircle
                } else {
                    PlayCircle
                }
            },
            onClick = ::handlePlayClick
        )
    )

    val columnDefs = useMemo(isPlaying, playTrack) {
        arrayOf<ColumnDef<Track, Any>>(
            jso {
                id = "title"
                header = StringOrTemplateHeader("Title")
                accessorFn = { row, _ -> row.title }
            },
            jso {
                id = "artist"
                header = StringOrTemplateHeader("Artist")
                accessorFn = { row, _ -> row.artist }
            },
            jso {
                id = "genre"
                header = StringOrTemplateHeader("Genre")
                accessorFn = { row, _ -> row.genre }
            },
            jso {
                id = "album"
                header = StringOrTemplateHeader("Album")
                accessorFn = { row, _ -> row.album }
            },
            jso {
                id = "lyricist"
                header = StringOrTemplateHeader("Lyricist")
                accessorFn = { row, _ -> row.lyricist }
            },
            jso {
                id = "comment"
                header = StringOrTemplateHeader("Comment")
                accessorFn = { row, _ -> row.comment }
            },
            jso {
                id = "bitrate"
                header = StringOrTemplateHeader("Bitrate")
                accessorFn = { row, _ -> row.bitrate }
            },
            jso {
                id = "year"
                header = StringOrTemplateHeader("Year")
                accessorFn = { row, _ -> row.year }
            },
            jso {
                id = "color"
                header = StringOrTemplateHeader("Color")
                cell = ColumnDefTemplate { ctx ->
                    ctx.row.original.color?.let { rbColor ->
                        createElement(Circle, jso {
                            sx {
                                color = Color(rbColor.name.lowercase())
                            }
                        })
                    } ?: CircleOutlined.create()
                }
            },
            jso {
                id = "bpm"
                header = StringOrTemplateHeader("BPM")
                accessorFn = { row, _ -> row.bpm.toString() }
            },
            jso {
                id = "rating"
                header = StringOrTemplateHeader("Rating")
                cell = ColumnDefTemplate { ctx ->
                    createElement(Rating, jso {
                        value = ctx.row.original.rating ?: 0
                        readOnly = true
                    })
                }
            },
            jso {
                id = "tags"
                header = StringOrTemplateHeader("Tags")
                accessorFn = { row, _ -> row.tags?.joinToString(", ") ?: "" }
            },
            jso {
                id = "length"
                header = StringOrTemplateHeader("Length")
                accessorFn = { row, _ -> row.length.seconds.toString() }
            },
            jso {
                id = "type"
                header = StringOrTemplateHeader("Type")
                accessorFn = { row, _ -> row.type.name }
            },
            jso {
                id = "action"
                header = StringOrTemplateHeader("Action")
                cell = rowActionTemplate(rowActions)
            }
        )
    }

    val table = useReactTable<Track>(
        options = jso {
            data = tracks
            columns = columnDefs
            getCoreRowModel = getCoreRowModel()
        }
    )

    println("render")

    Box {
        Paper {
            TableContainer {
                sx {
                    MuiRating.root {
                        color = theme.palette.primary.main
                    }
                }

                SearchTableToolbar {
                    title = "Rekordbox Library"
                    this.searchText = searchText
                    this.setSearchText = setSearchText
                    isFetching = search.isFetching
                }
                BasicTable {
                    this.table = table
                    this.selectable = false
                    this.sortable = false
                }
            }
        }
    }
}
