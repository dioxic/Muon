package uk.dioxic.muon

import com.ccfraser.muirwik.components.MColor
import com.ccfraser.muirwik.components.button.MButtonVariant
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.dialog.*
import com.ccfraser.muirwik.components.form.MFormControlMargin
import com.ccfraser.muirwik.components.mTextField
import kotlinx.html.InputType
import org.w3c.dom.HTMLInputElement
import react.RProps
import react.functionalComponent

external interface MusicEditProps : RProps {
    var audioFile: AudioFile?
    var onChange: (AudioFile) -> Unit
    var onClose: () -> Unit
    var onSave: () -> Unit
    var open: Boolean
}

val MusicEdit = functionalComponent<MusicEditProps> { props ->

    mDialog(props.open, onClose = { _, _ -> props.onClose() }) {
        mDialogTitle("Edit")
        mDialogContent {
//            mDialogContentText("To edit this audio file, change stuff here.")
            mTextField(
                label = "Artist",
                autoFocus = true,
                margin = MFormControlMargin.dense,
                type = InputType.text,
                fullWidth = true,
                value = props.audioFile?.artist,
                onChange = { props.onChange(props.audioFile!!.copy(artist = (it.target as HTMLInputElement).value)) }
            )
            mTextField(
                label = "Title",
                autoFocus = true,
                margin = MFormControlMargin.normal,
                type = InputType.text,
                fullWidth = true,
                value = props.audioFile?.title,
                onChange = { props.onChange(props.audioFile!!.copy(title = (it.target as HTMLInputElement).value)) }
            )
            mTextField(
                label = "Album",
                autoFocus = true,
                margin = MFormControlMargin.normal,
                type = InputType.text,
                fullWidth = true,
                value = props.audioFile?.album,
                onChange = { props.onChange(props.audioFile!!.copy(album = (it.target as HTMLInputElement).value)) }
            )
            mTextField(
                label = "Lyricist",
                autoFocus = true,
                margin = MFormControlMargin.normal,
                type = InputType.text,
                fullWidth = true,
                value = props.audioFile?.lyricist,
                onChange = { props.onChange(props.audioFile!!.copy(lyricist = (it.target as HTMLInputElement).value)) }
            )
            mTextField(
                label = "Year",
                autoFocus = true,
                margin = MFormControlMargin.normal,
                type = InputType.text,
                fullWidth = true,
                value = props.audioFile?.year,
                onChange = { props.onChange(props.audioFile!!.copy(year = (it.target as HTMLInputElement).value)) }
            )
            mTextField(
                label = "Original Filename",
                autoFocus = true,
                disabled = true,
                margin = MFormControlMargin.normal,
                type = InputType.text,
                fullWidth = true,
                value = props.audioFile?.originalFilename
            )
            mTextField(
                label = "New Filename",
                autoFocus = true,
                margin = MFormControlMargin.normal,
                type = InputType.text,
                fullWidth = true,
                value = props.audioFile?.newFilename,
                onChange = { props.onChange(props.audioFile!!.copy(newFilename = (it.target as HTMLInputElement).value)) }
            )
            mTextField(
                label = "Comment",
                autoFocus = true,
                margin = MFormControlMargin.normal,
                type = InputType.text,
                fullWidth = true,
                value = props.audioFile?.comment,
                onChange = { props.onChange(props.audioFile!!.copy(comment = (it.target as HTMLInputElement).value)) }
            )
        }
        mDialogActions {
            mButton("Cancel", color = MColor.primary, onClick = { props.onClose() }, variant = MButtonVariant.text)
            mButton("Save", color = MColor.primary, onClick = { props.onSave() }, variant = MButtonVariant.text)
        }
    }
}