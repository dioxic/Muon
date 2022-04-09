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
import uk.dioxic.muon.hook.useSaveTrack
import uk.dioxic.muon.model.Track

external interface TrackDialogProps : Props {
    var track: Track
    var open: Boolean
    var handleClose: () -> Unit
}

val TrackEditDialog = FC<TrackDialogProps> { props ->
    val saveTrack = useSaveTrack()
    var track = props.track.copy()

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
                    saveTrack(track)
                    props.handleClose()
                }

                TextField {
                    id = "title"
                    label = ReactNode("Title")
                    type = InputType.text
                    variant = FormControlVariant.outlined
                    defaultValue = track.title
                    onChange = { event -> track = track.copy(title = (event.target as HTMLInputElement).value) }
                    fullWidth = true
                }
                TextField {
                    id = "artist"
                    label = ReactNode("Artist")
                    type = InputType.text
                    variant = FormControlVariant.outlined
                    defaultValue = track.artist
                    onChange = { event -> track = track.copy(artist = (event.target as HTMLInputElement).value) }
                    fullWidth = true
                }
                TextField {
                    id = "lyricist"
                    label = ReactNode("Lyricist")
                    type = InputType.text
                    variant = FormControlVariant.outlined
                    defaultValue = track.lyricist
                    onChange = { event -> track = track.copy(lyricist = (event.target as HTMLInputElement).value) }
                    fullWidth = true
                }
                TextField {
                    id = "album"
                    label = ReactNode("Album")
                    type = InputType.text
                    variant = FormControlVariant.outlined
                    defaultValue = track.album
                    onChange = { event -> track = track.copy(album = (event.target as HTMLInputElement).value) }
                    fullWidth = true
                }
                TextField {
                    id = "comment"
                    label = ReactNode("Comment")
                    type = InputType.text
                    variant = FormControlVariant.outlined
                    defaultValue = track.comment
                    onChange = { event -> track = track.copy(comment = (event.target as HTMLInputElement).value) }
                    fullWidth = true
                }
                TextField {
                    id = "filename"
                    label = ReactNode("Filename")
                    type = InputType.text
                    variant = FormControlVariant.outlined
                    defaultValue = track.filename
                    onChange = { event -> track = track.copy(filename = (event.target as HTMLInputElement).value) }
                    fullWidth = true
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