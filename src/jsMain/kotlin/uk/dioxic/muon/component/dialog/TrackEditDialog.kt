package uk.dioxic.muon.component.dialog

import js.core.jso
import mui.material.*
import mui.system.sx
import org.w3c.dom.HTMLInputElement
import react.FC
import react.Props
import react.ReactNode
import react.dom.html.ReactHTML.form
import react.dom.onChange
import react.useContext
import uk.dioxic.muon.context.SettingsContext
import uk.dioxic.muon.hook.useTrackSave
import uk.dioxic.muon.model.Track
import web.cssom.px
import web.html.ButtonType
import web.html.InputType

external interface TrackDialogProps : Props {
    var tracks: Array<out Track>
    var open: Boolean
    var handleClose: () -> Unit
}

const val MULTIPLE: String = "MULTIPLE"

private val defaultTextProps: BaseTextFieldProps = jso {
    type = InputType.text
    spellCheck = false
    variant = FormControlVariant.outlined
    fullWidth = true
    autoComplete = "off"
}

val TrackEditDialog = FC<TrackDialogProps> { props ->
    val (settings) = useContext(SettingsContext)!!
    val saveTrack = useTrackSave()
    var editTrack = props.tracks.merge()

    fun handleSave() {
        props.tracks.forEach { original ->
            println("saving track")
            saveTrack(assign(original, editTrack))
        }
        props.handleClose()
    }

    Dialog {
        open = props.open
        fullWidth = true
        onClose = { _, _ -> props.handleClose() }

        DialogTitle {
            +"Edit Track"
        }

        DialogContent {
            sx {
                paddingTop = 10.px

                MuiTextField.root {
                    marginTop = 20.px
                }
            }

            form {
                id = "myForm"
                onSubmit = { event ->
                    event.preventDefault()
                    handleSave()
                }

                TextField {
                    id = "title"
                    label = ReactNode("Title")
                    defaultValue = editTrack.title
                    onChange = { event -> editTrack = editTrack.copy(title = (event.target.asDynamic() as HTMLInputElement).value) }

                    +defaultTextProps
                }
                TextField {
                    id = "artist"
                    label = ReactNode("Artist")
                    defaultValue = editTrack.artist
                    onChange =
                        { event -> editTrack = editTrack.copy(artist = (event.target.asDynamic() as HTMLInputElement).value) }

                    +defaultTextProps
                }
                TextField {
                    id = "lyricist"
                    label = ReactNode("Lyricist")
                    defaultValue = editTrack.lyricist
                    onChange =
                        { event -> editTrack = editTrack.copy(lyricist = (event.target.asDynamic() as HTMLInputElement).value) }

                    +defaultTextProps
                }
                TextField {
                    id = "album"
                    label = ReactNode("Album")
                    defaultValue = editTrack.album
                    onChange = { event -> editTrack = editTrack.copy(album = (event.target.asDynamic() as HTMLInputElement).value) }

                    +defaultTextProps
                }
                TextField {
                    id = "comment"
                    label = ReactNode("Comment")
                    defaultValue = editTrack.comment
                    onChange =
                        { event -> editTrack = editTrack.copy(comment = (event.target.asDynamic() as HTMLInputElement).value) }

                    +defaultTextProps
                }
                TextField {
                    id = "genre"
                    label = ReactNode("Genre")
                    defaultValue = editTrack.genre
                    onChange =
                        { event -> editTrack = editTrack.copy(genre = (event.target.asDynamic() as HTMLInputElement).value) }

                    +defaultTextProps
                }
                if (props.tracks.size == 1) {
                    TextField {
                        id = "filename"
                        label = ReactNode("Filename")
                        defaultValue = editTrack.filename
                        disabled = settings.standardiseFilenames
                        onChange =
                            { event -> editTrack = editTrack.copy(filename = (event.target.asDynamic() as HTMLInputElement).value) }

                        +defaultTextProps
                    }
                }
            }
        }

        DialogActions {
            Button {
                onClick = { _ ->
                    props.handleClose()
                }
                +"Cancel"
            }
            Button {
                type = ButtonType.submit
                form = "myForm"
                +"Save"
            }
        }
    }
}

private fun Array<out Track>.merge(): Track {
    var track = if (isEmpty()) Track.EMPTY else first()

    forEach {
        track = track.copy(
            artist = merge(track, it) { artist },
            title = merge(track, it) { title },
            lyricist = merge(track, it) { lyricist },
            album = merge(track, it) { album },
            comment = merge(track, it) { comment },
            genre = merge(track, it) { genre },
            filename = merge(track, it) { filename },
        )
    }
    return track
}

private fun merge(a: Track, b: Track, accessor: Track.() -> String) =
    if (accessor.invoke(a) == accessor.invoke(b)) accessor.invoke(a) else MULTIPLE

private fun assign(target: Track, source: Track, accessor: Track.() -> String) =
    if (accessor.invoke(source) == MULTIPLE) accessor.invoke(target) else accessor.invoke(source)

private fun assign(target: Track, source: Track) =
    target.copy(
        artist = assign(target, source) { artist },
        title = assign(target, source) { title },
        lyricist = assign(target, source) { lyricist },
        album = assign(target, source) { album },
        comment = assign(target, source) { comment },
        genre = assign(target, source) { genre },
        filename = assign(target, source) { filename },
    )