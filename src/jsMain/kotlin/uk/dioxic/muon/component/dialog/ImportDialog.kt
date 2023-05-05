package uk.dioxic.muon.component.dialog

import mui.material.*
import react.FC
import react.Props
import uk.dioxic.muon.model.Track

external interface ImportDialogProps : Props {
    var tracks: Array<out Track>
    var open: Boolean
    var handleImport: (Array<out Track>) -> Unit
    var handleClose: () -> Unit
}

val ImportDialog = FC<ImportDialogProps> { props ->
    Dialog {
        open = props.open
        fullWidth = true
        onClose = { _, _ -> props.handleClose() }
        val withoutDuplicates = props.tracks.filter { it.duplicates.isNullOrEmpty() }
        val withDuplicates = props.tracks.filterNot { it.duplicates.isNullOrEmpty() }

        DialogTitle {
            +"Import"
        }
        DialogContent {
            DialogContentText {
                +if (withDuplicates.size == 1) {
                    "${withDuplicates.first().artist} - ${withDuplicates.first().title} has potential duplicates - are you sure you want to import?"
                } else {
                    "${withDuplicates.size} tracks have potential duplicates - are you sure you want to import?"
                }
            }
        }
        DialogActions {
            Button {
                onClick = { _ ->
                    props.handleClose()
                    props.handleImport(props.tracks)
                }
                +"Import All"
            }
            if (withoutDuplicates.isNotEmpty()) {
                Button {
                    onClick = { _ ->
                        props.handleClose()
                        props.handleImport(withoutDuplicates.toTypedArray())
                    }
                    +"Import Safe"
                }
            }
            Button {
                onClick = { _ ->
                    props.handleClose()
                }
                +"Cancel"
            }
        }
    }
}