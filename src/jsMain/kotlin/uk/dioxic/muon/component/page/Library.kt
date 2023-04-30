package uk.dioxic.muon.component.page

import csstype.*
import js.core.jso
import kotlinx.browser.window
import mui.icons.material.Circle
import mui.icons.material.CircleOutlined
import mui.icons.material.PlayCircle
import mui.icons.material.Search
import mui.material.*
import mui.material.Size
import mui.material.styles.Theme
import mui.material.styles.TypographyVariant
import mui.material.styles.useTheme
import mui.system.sx
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.html.ReactHTML
import tanstack.react.table.useReactTable
import tanstack.table.core.ColumnDef
import tanstack.table.core.StringOrTemplateHeader
import tanstack.table.core.getCoreRowModel
import uk.dioxic.muon.Routes
import uk.dioxic.muon.common.debounce
import uk.dioxic.muon.component.table.actions.RowAction
import uk.dioxic.muon.component.table.columns
import uk.dioxic.muon.external.chroma
import uk.dioxic.muon.hook.useTrackSearch
import uk.dioxic.muon.model.Track
import web.cssom.*
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

private val columnDefs = columns<Track> {
    column(
        id = "title",
        header = "Title",
        accessor = { title },
    )
    column(
        id = "artist",
        header = "Artist",
        accessor = { artist },
    )
    column(
        id = "genre",
        header = "Genre",
        accessor = { genre },
    )
    column(
        id = "album",
        header = "Album",
        accessor = { album },
    )
    column(
        id = "lyricist",
        header = "Lyricist",
        accessor = { lyricist },
    )
    column(
        id = "comment",
        header = "Comment",
        accessor = { comment },
    )
    column(
        id = "bitrate",
        header = "Bitrate",
        accessor = { bitrate },
    )
    column(
        id = "year",
        header = "Year",
        accessor = { year },
    )
    column(
        id = "color",
        header = "Color",
        accessor = { color },
        render = {
            it.getValue()?.let { rbColor ->
                createElement(Circle, jso {
                    sx {
                        color = Color(rbColor.name.lowercase())
                    }
                })
            } ?: CircleOutlined.create()
        },
    )
    column(
        id = "bpm",
        header = "BPM",
        accessor = { bpm },
    )
    column(
        id = "rating",
        header = "Rating",
        accessor = { rating ?: 0 },
        render = {
            createElement(Rating, jso {
                value = it.getValue()
                readOnly = true
            })
        },
    )
    column(
        id = "tags",
        header = "Tags",
        accessor = { tags ?: emptyList() },
        render = {
            it.getValue().joinToString(", ").unsafeCast<ReactElement<*>>()
        },
    )
    column(
        id = "length",
        header = "Length",
        accessor = { length.seconds.toString() },
    )
    column(
        id = "type",
        header = "Type",
        accessor = { type },
    )
}

private var columnDefinitions = arrayOf<ColumnDef<Track, Any>>(
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
        accessorFn = { row, _ -> row.year }
//        cell = {
//            it.value?.let { rbColor ->
//                createElement(Circle, jso {
//                    sx {
//                        color = Color(rbColor.name.lowercase())
//                    }
//                })
//            } ?: CircleOutlined.create()
//        }
    },
    jso {
        id = "bpm"
        header = StringOrTemplateHeader("BPM")
        accessorFn = { row, _ -> row.bpm.toString() }
    },
    jso {
        id = "rating"
        header = StringOrTemplateHeader("Rating")
        accessorFn = { row, _ -> row.rating.toString() }
//        cell = {
//            createElement(Rating, jso {
//                value = it.value ?: 0
//                readOnly = true
//            })
//        }
    },
    jso {
        id = "tags"
        header = StringOrTemplateHeader("Tags")
        accessorFn = { row, _ -> row.tags ?: emptyList<String>() }
//        cell = {
//            (it.value?.joinToString(", ") ?: "").unsafeCast<ReactElement<*>>()
//        },
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
)

val LibraryPage = VFC {
    val theme = useTheme<Theme>()
    val (searchText, setSearchText) = useState("")
    val tracks = useTrackSearch(searchText)

    fun handlePlay(track: Track) {
        window.open(Routes.trackAudio(track), "_blank")?.focus()
    }

    val rowActions = listOf(
        RowAction(
            name = "Play",
            icon = PlayCircle,
            onClick = ::handlePlay
        )
    )

    val table = useReactTable<Track>(
        options = jso {
            data = tracks.data?.toTypedArray() ?: emptyArray()
            columns = columnDefs
            getCoreRowModel = getCoreRowModel()
        }
    )

    println("render")

    Box {
        Paper {
            TableContainer {
                Toolbar {
                    Typography {
                        sx {
                            flexGrow = number(1.0)
                        }
                        component = ReactHTML.div
                        variant = TypographyVariant.h6
                        +"Rekordbox Library"
                    }
                    Paper {
                        sx {
                            display = Display.flex
                            alignItems = AlignItems.center
                            width = 400.px
                            backgroundColor = chroma(theme.palette.primary.main)
                                .alpha(theme.palette.action.activatedOpacity)
                                .hex()
                        }

                        InputBase {
                            sx {
                                flex = Flex.fitContent
                                marginLeft = 1.em
                            }
                            spellCheck = false
                            autoComplete = "off"
                            placeholder = "Search Library"
                            onChange = { event ->
                                debounce((event.target as HTMLInputElement).value, 500) {
                                    if (it != searchText && (it.length > 3 || it.isEmpty())) {
                                        setSearchText(it)
                                    }
                                }
                            }
                        }
                        Box {
                            sx {
                                position = Position.relative
                                padding = 8.px
                                MuiSvgIcon.root {
                                    verticalAlign = VerticalAlign.middle
                                }
                            }
                            Search()

                            if (tracks.isFetching) {
                                CircularProgress {
                                    size = 36.px
                                    color = CircularProgressColor.success

                                    sx {
                                        position = Position.absolute
                                        top = 2.px
                                        left = 2.px
                                        zIndex = integer(1)
                                    }

                                    variant = CircularProgressVariant.indeterminate
                                }
                            }
                        }
                    }
                }
                Table {
                    sx {
                        MuiRating.root {
                            color = theme.palette.primary.main
                        }
                    }
                    size = Size.small


                }
            }
        }
    }
}
