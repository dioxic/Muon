package uk.dioxic.muon.component.dialog

import js.core.jso
import mui.icons.material.Delete
import mui.icons.material.GetApp
import mui.icons.material.KeyboardArrowDown
import mui.icons.material.KeyboardArrowUp
import mui.material.*
import mui.material.styles.Theme
import mui.material.styles.useTheme
import mui.system.sx
import react.FC
import react.Props
import react.ReactNode
import react.useState
import uk.dioxic.muon.component.table.actions.RowAction
import uk.dioxic.muon.external.chroma
import uk.dioxic.muon.hook.useImportDelete
import uk.dioxic.muon.hook.useImportMutation
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.model.Tracks
import web.cssom.FontFamily
import web.cssom.FontSize
import web.cssom.px
import kotlin.time.Duration.Companion.seconds

external interface ImportDuplicateProps : Props {
    var tracks: Tracks
    var open: Boolean
    var handleClose: () -> Unit
}

@Deprecated("not bothering with dialog anymore")
val ImportDuplicateDialog = FC<ImportDuplicateProps> { props ->
    val import = useImportMutation()
    val delete = useImportDelete()

    fun handleImportClick(track: Track) {
        import.mutate(arrayOf(track), jso())
    }

    fun handleDeleteDuplicateClick(track: Track) {
        delete(track)
    }

    val trackActions = listOf(
        RowAction(name = "import", icon = GetApp, onClick = ::handleImportClick)
    )

    val duplicateActions = listOf(
        RowAction(name = "delete",
            icon = Delete,
            onClick = ::handleDeleteDuplicateClick,
            iconColor = IconButtonColor.error)
    )

    Dialog {
        open = props.open
        fullWidth = true
        maxWidth = "lg"
        onClose = { _, _ -> props.handleClose() }

        DialogTitle {
            +"Potential Duplicates"
        }

        DialogContent {
            sx {
                fontSize = FontSize.small
                fontFamily = FontFamily.monospace
            }

            CollapsibleTable {
                importTracks = props.tracks
                this.trackActions = trackActions
                this.duplicateActions = duplicateActions
            }
        }

        DialogActions {
            Button {
                onClick = { _ ->
                    props.handleClose()
                }
                +"Close"
            }
        }
    }
}

external interface CollapsibleTableProps : Props {
    var importTracks: Tracks
    var trackActions: List<RowAction<Track>>
    var duplicateActions: List<RowAction<Track>>
}

val CollapsibleTable = FC<CollapsibleTableProps> { props ->
    TableContainer {
        Table {
            size = Size.small
            TableHead {
                TableRow {
                    TableCell { }
                    TableCell { +"Title" }
                    TableCell { +"Artist" }
                    TableCell { +"Lyricist" }
                    TableCell { +"Bitrate" }
                    TableCell { +"Length" }
                    TableCell { +"Type" }
                    TableCell { }
                }
            }
            TableBody {
                props.importTracks.forEach {
                    CollapsibleRow {
                        track = it
                        trackActions = props.trackActions
                        duplicateActions = props.duplicateActions
                    }
                }
            }
        }
    }
}

external interface CollapsibleRowProps : Props {
    var track: Track
    var trackActions: List<RowAction<Track>>
    var duplicateActions: List<RowAction<Track>>
}

val CollapsibleRow = FC<CollapsibleRowProps> { props ->
    val theme = useTheme<Theme>()
    val (open, setOpen) = useState(false)

    TableRow {
        sx {
            MuiTableCell.root {
                borderBottom = 0.px
            }
        }
        TableCell {
            IconButton {
                size = Size.small
                onClick = { _ -> setOpen(!open) }

                if (open) {
                    KeyboardArrowUp()
                } else {
                    KeyboardArrowDown()
                }
            }
        }
        TableCell { +props.track.title }
        TableCell { +props.track.artist }
        TableCell { +props.track.lyricist }
        TableCell { +props.track.bitrate.toString() }
        TableCell { +props.track.length.seconds.toString() }
        TableCell { +props.track.type.toString() }
//        TableCell {
//            props.trackActions.forEach { action ->
//                Tooltip {
//                    title = ReactNode(action.name)
//                    IconButton {
//                        action.iconColor?.let {
//                            color = it
//                        }
//                        size = Size.small
//                        onClick = { event ->
//                            event.stopPropagation()
//                            action.onClick(props.track)
//                        }
//
//                        action.icon()
//                    }
//                }
//            }
//        }
    }
    TableRow {
        TableCell {
            sx {
                paddingBottom = 0.px
                paddingTop = 0.px
                backgroundColor = chroma(theme.palette.primary.main)
                    .alpha(theme.palette.action.activatedOpacity)
                    .hex()
            }

            colSpan = 8
            Collapse {
                `in` = open
                Box {
                    sx {
                        margin = 8.px
                    }
                    SubTable {
                        matchedTracks = props.track.duplicates ?: emptyList()
                        trackActions = props.duplicateActions
                    }
                }
            }
        }
    }
}

external interface SubTableProps : Props {
    var matchedTracks: Tracks
    var trackActions: List<RowAction<Track>>
}

val SubTable = FC<SubTableProps> { props ->
    Table {
        size = Size.small
        TableHead {
            TableRow {
                TableCell { +"Title" }
                TableCell { +"Artist" }
                TableCell { +"Lyricist" }
                TableCell { +"Bitrate" }
                TableCell { +"Length" }
                TableCell { +"Type" }
                TableCell { +"Location" }
                TableCell { }
            }
        }
        TableBody {
            props.matchedTracks.forEach { track ->
                TableRow {
                    TableCell { +track.title }
                    TableCell { +track.artist }
                    TableCell { +track.lyricist }
                    TableCell { +track.bitrate.toString() }
                    TableCell { +track.length.seconds.toString() }
                    TableCell { +track.type.toString() }
                    TableCell { +track.path }
//                    TableCell {
//                        props.trackActions.forEach { action ->
//                            Tooltip {
//                                title = ReactNode(action.name)
//                                IconButton {
//                                    action.iconColor?.let {
//                                        color = it
//                                    }
//                                    size = Size.small
//                                    onClick = { event ->
//                                        event.stopPropagation()
//                                        action.onClick(track)
//                                    }
//
//                                    action.icon()
//                                }
//                            }
//                        }
//                    }
                }
            }
        }
    }
}