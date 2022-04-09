package uk.dioxic.muon.component.dialog

import csstype.PaddingTop
import csstype.px
import mui.material.*
import mui.system.sx
import org.w3c.dom.HTMLInputElement
import react.FC
import react.Props
import react.ReactNode
import react.dom.html.InputType
import react.useState
import uk.dioxic.muon.hook.useSaveTrack
import uk.dioxic.muon.model.Track

external interface TrackDialogProps : Props {
    var track: Track
    var open: Boolean
    var handleClose: () -> Unit
}

val TrackEditDialog = FC<TrackDialogProps> { props ->
    val (track, setTrack) = useState(props.track)
    val saveTrack = useSaveTrack()

    Dialog {
        open = props.open
        fullWidth = true

        DialogTitle {
            +"Edit Track"
        }

        DialogContent {

            sx {
                paddingTop = 10.px

                MuiTextField.root {
                    marginTop = 20.px
//                    marginBottom = 10.px
                }
            }

            TextField {
                id = "title"
                label = ReactNode("Title")
                type = InputType.text
                variant = FormControlVariant.outlined
                defaultValue = track.title
                onBlur = { event -> setTrack(track.copy(title = (event.target as HTMLInputElement).value)) }
                fullWidth = true
            }
            TextField {
                id = "artist"
                label = ReactNode("Artist")
                type = InputType.text
                variant = FormControlVariant.outlined
                defaultValue = track.artist
                onBlur = { event -> setTrack(track.copy(artist = (event.target as HTMLInputElement).value)) }
                fullWidth = true
            }
            TextField {
                id = "lyricist"
                label = ReactNode("Lyricist")
                type = InputType.text
                variant = FormControlVariant.outlined
                defaultValue = track.lyricist
                onBlur = { event -> setTrack(track.copy(lyricist = (event.target as HTMLInputElement).value)) }
                fullWidth = true
            }
            TextField {
                id = "album"
                label = ReactNode("Album")
                type = InputType.text
                variant = FormControlVariant.outlined
                defaultValue = track.album
                onBlur = { event -> setTrack(track.copy(album = (event.target as HTMLInputElement).value)) }
                fullWidth = true
            }
            TextField {
                id = "comment"
                label = ReactNode("Comment")
                type = InputType.text
                variant = FormControlVariant.outlined
                defaultValue = track.comment
                onBlur = { event -> setTrack(track.copy(comment = (event.target as HTMLInputElement).value)) }
                fullWidth = true
            }
            TextField {
                id = "filename"
                label = ReactNode("Filename")
                type = InputType.text
                variant = FormControlVariant.outlined
                defaultValue = track.filename
                onBlur = { event -> setTrack(track.copy(filename = (event.target as HTMLInputElement).value)) }
                fullWidth = true
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
                onClick = { _ ->
                    saveTrack(track)
                    props.handleClose()
                }
                +"Save"
            }
        }
    }
}