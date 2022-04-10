package uk.dioxic.muon.component.dialog

import csstype.px
import mui.material.*
import mui.system.sx
import org.w3c.dom.HTMLInputElement
import react.FC
import react.Props
import react.ReactNode
import react.dom.html.ButtonType
import react.dom.html.InputType
import react.dom.html.ReactHTML.form
import react.dom.onChange
import uk.dioxic.muon.hook.useImportSave
import uk.dioxic.muon.model.Track

external interface TrackDialogProps : Props {
    var tracks: List<Track>
    var open: Boolean
    var handleClose: () -> Unit
}

const val MULTIPLE: String = "MULTIPLE"

fun List<Track>.merge(): Track {
    require(isNotEmpty())
    var track = first()

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

fun merge(a: Track, b: Track, accessor: Track.() -> String) =
    if (accessor.invoke(a) == accessor.invoke(b)) accessor.invoke(a) else MULTIPLE

fun assign(target: Track, source: Track, accessor: Track.() -> String) =
    if (accessor.invoke(source) == MULTIPLE) accessor.invoke(target) else accessor.invoke(source)

fun assign(target: Track, source: Track) =
    target.copy(
        artist = assign(target, source) { artist },
        title = assign(target, source) { title },
        lyricist = assign(target, source) { lyricist },
        album = assign(target, source) { album },
        comment = assign(target, source) { comment },
        genre = assign(target, source) { genre },
        filename = assign(target, source) { filename },
    )

val TrackEditDialog = FC<TrackDialogProps> { props ->
    val saveTrack = useImportSave()
    var editTrack = props.tracks.merge()

    fun handleSave() {
        props.tracks.forEach { original ->
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
                    type = InputType.text
                    variant = FormControlVariant.outlined
                    defaultValue = editTrack.title
                    onChange = { event -> editTrack = editTrack.copy(title = (event.target as HTMLInputElement).value) }
                    fullWidth = true
                }
                TextField {
                    id = "artist"
                    label = ReactNode("Artist")
                    type = InputType.text
                    variant = FormControlVariant.outlined
                    defaultValue = editTrack.artist
                    onChange =
                        { event -> editTrack = editTrack.copy(artist = (event.target as HTMLInputElement).value) }
                    fullWidth = true
                }
                TextField {
                    id = "lyricist"
                    label = ReactNode("Lyricist")
                    type = InputType.text
                    variant = FormControlVariant.outlined
                    defaultValue = editTrack.lyricist
                    onChange =
                        { event -> editTrack = editTrack.copy(lyricist = (event.target as HTMLInputElement).value) }
                    fullWidth = true
                }
                TextField {
                    id = "album"
                    label = ReactNode("Album")
                    type = InputType.text
                    variant = FormControlVariant.outlined
                    defaultValue = editTrack.album
                    onChange = { event -> editTrack = editTrack.copy(album = (event.target as HTMLInputElement).value) }
                    fullWidth = true
                }
                TextField {
                    id = "comment"
                    label = ReactNode("Comment")
                    type = InputType.text
                    variant = FormControlVariant.outlined
                    defaultValue = editTrack.comment
                    onChange =
                        { event -> editTrack = editTrack.copy(comment = (event.target as HTMLInputElement).value) }
                    fullWidth = true
                }
                TextField {
                    id = "genre"
                    label = ReactNode("Genre")
                    type = InputType.text
                    variant = FormControlVariant.outlined
                    defaultValue = editTrack.genre
                    onChange =
                        { event -> editTrack = editTrack.copy(genre = (event.target as HTMLInputElement).value) }
                    fullWidth = true
                }
                if (props.tracks.size == 1) {
                    TextField {
                        id = "filename"
                        label = ReactNode("Filename")
                        type = InputType.text
                        variant = FormControlVariant.outlined
                        defaultValue = editTrack.filename
                        onChange =
                            { event -> editTrack = editTrack.copy(filename = (event.target as HTMLInputElement).value) }
                        fullWidth = true
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