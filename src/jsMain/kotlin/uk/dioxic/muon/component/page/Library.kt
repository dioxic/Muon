package uk.dioxic.muon.component.page

import csstype.*
import kotlinx.js.jso
import mui.icons.material.Circle
import mui.icons.material.CircleOutlined
import mui.icons.material.Search
import mui.material.*
import mui.material.Size
import mui.material.styles.Theme
import mui.material.styles.TypographyVariant
import mui.material.styles.useTheme
import mui.system.sx
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.html.ButtonType
import react.dom.html.ReactHTML
import react.table.*
import uk.dioxic.muon.common.debounce
import uk.dioxic.muon.context.AlertContext
import uk.dioxic.muon.external.chroma
import uk.dioxic.muon.hook.useTrackSearch
import uk.dioxic.muon.model.Track
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
private val columnDefinitions = columns<Track> {
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
        cell = {
            it.value?.let { rbColor ->
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
        accessor = { rating },
        cell = {
            createElement(Rating, jso {
                value = it.value ?: 0
                readOnly = true
//                    icon = createElement(Star, jso {
//                        color = chroma(theme.palette.primary.main)
//                            .alpha(theme.palette.action.activatedOpacity)
//                            .hex()
//                    })
//                    emptyIcon = StarOutline.create()
            })
        },
    )
    column(
        id = "tags",
        header = "Tags",
        accessor = { tags },
        cell = {
            (it.value?.joinToString(", ") ?: "").unsafeCast<ReactElement<*>>()
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

val LibraryPage = VFC {
    val theme = useTheme<Theme>()
    val (searchText, setSearchText) = useState<String?>(null)
    val tracks = useTrackSearch(searchText ?: "")
    val (_, addAlert) = useContext(AlertContext)

    val table = useTable<Track>(
        options = jso {
            data = useMemo(tracks.data) { tracks.data?.toTypedArray() ?: emptyArray() }
            columns = useMemo { columnDefinitions }
        },
        useSortBy,
        useColumnOrder
    )

    fun handleSearch() {
        searchText?.let {
//            setSearchText(it)
            addAlert(uk.dioxic.muon.context.Alert.AlertSuccess(it))
        }
    }

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

                        component = ReactHTML.form
                        onSubmit = { event ->
                            event.preventDefault()
                            handleSearch()
                        }

                        InputBase {
                            sx {
                                flex = Flex.fitContent
                                marginLeft = 1.em
                            }
                            placeholder = "Search Library"
                            onChange = { event ->
                                debounce((event.target as HTMLInputElement).value, 800) {
                                    if (it != searchText && it.length > 3) {
                                        setSearchText(it)
                                    }
                                }
                            }
                        }
                        IconButton {
                            type = ButtonType.submit

                            Search()
                        }
                    }
                }
                LibraryTable {
                    tableInstance = table
                }
            }
        }
    }

}

external interface LibraryTableProps : Props {
    var tableInstance: TableInstance<Track>
}

val LibraryTable = FC<LibraryTableProps> { props ->
    val theme = useTheme<Theme>()

    Table {
        sx {
            MuiRating.root {
                color = theme.palette.primary.main
            }
        }
        size = Size.small

        +props.tableInstance.getTableProps()

        TableHead {
            props.tableInstance.headerGroups.forEach { headerGroup ->
                TableRow {
                    +headerGroup.getHeaderGroupProps()

                    headerGroup.headers.forEach { header ->
                        TableCell {
                            sx {
                                minWidth = minimumWidths[header.id]!!.px
                            }

                            +header.getHeaderProps(header.getSortByToggleProps())
                            +header.render(RenderType.Header)

                            TableSortLabel {
                                active = header.isSorted
                                direction = if (header.isSortedDesc)
                                    TableSortLabelDirection.desc
                                else
                                    TableSortLabelDirection.asc
                            }
                        }
                    }
                }
            }
        }

        TableBody {
            props.tableInstance.rows.forEach { row ->
                props.tableInstance.prepareRow(row)
                TableRow {
                    row.cells.forEach { cell ->
                        TableCell {
                            +cell.getCellProps()
                            +cell.render(RenderType.Cell)
                        }
                    }
                }
            }
        }
    }
}
