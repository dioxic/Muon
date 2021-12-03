package uk.dioxic.muon.component

import com.ccfraser.muirwik.components.MColor
import com.ccfraser.muirwik.components.button.MButtonVariant
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.dialog.mDialog
import com.ccfraser.muirwik.components.dialog.mDialogActions
import com.ccfraser.muirwik.components.dialog.mDialogContent
import com.ccfraser.muirwik.components.dialog.mDialogTitle
import com.ccfraser.muirwik.components.form.MFormControlMargin
import com.ccfraser.muirwik.components.mTextField
import kotlinx.html.InputType
import org.w3c.dom.HTMLInputElement
import react.Props
import react.fc
import uk.dioxic.muon.audio.AudioFile

external interface MusicEditProps : Props {
    var initialState: AudioFile
    var title: String
    var onChange: (AudioFile) -> Unit
    var onClose: () -> Unit
    var onSave: () -> Unit
    var open: Boolean
    var showFilename: Boolean
}

val MusicEdit = fc<MusicEditProps> { props ->

    mDialog(props.open, onClose = { _, _ -> props.onClose() }) {
        mDialogTitle(props.title)
        mDialogContent {
//            mDialogContentText("To edit this audio file, change stuff here.")
            mTextField(
                label = "Artist",
                autoFocus = true,
                margin = MFormControlMargin.dense,
                type = InputType.text,
                fullWidth = true,
                value = props.initialState.tags.artist,
                onChange = {
                    props.onChange(
                        props.initialState.copy(
                            tags = props.initialState.tags.copy(artist = (it.target as HTMLInputElement).value)
                        )
                    )
                }
            )
            mTextField(
                label = "Title",
                autoFocus = true,
                margin = MFormControlMargin.normal,
                type = InputType.text,
                fullWidth = true,
                value = props.initialState.tags.title,
                onChange = {
                    props.onChange(
                        props.initialState.copy(
                            tags = props.initialState.tags.copy(title = (it.target as HTMLInputElement).value)
                        )
                    )
                }
            )
            mTextField(
                label = "Album",
                autoFocus = true,
                margin = MFormControlMargin.normal,
                type = InputType.text,
                fullWidth = true,
                value = props.initialState.tags.album,
                onChange = {
                    props.onChange(
                        props.initialState.copy(
                            tags = props.initialState.tags.copy(album = (it.target as HTMLInputElement).value)
                        )
                    )
                }
            )
            mTextField(
                label = "Lyricist",
                autoFocus = true,
                margin = MFormControlMargin.normal,
                type = InputType.text,
                fullWidth = true,
                value = props.initialState.tags.lyricist,
                onChange = {
                    props.onChange(
                        props.initialState.copy(
                            tags = props.initialState.tags.copy(lyricist = (it.target as HTMLInputElement).value)
                        )
                    )
                }
            )
            mTextField(
                label = "Year",
                autoFocus = true,
                margin = MFormControlMargin.normal,
                type = InputType.text,
                fullWidth = true,
                value = props.initialState.tags.year,
                onChange = {
                    props.onChange(
                        props.initialState.copy(
                            tags = props.initialState.tags.copy(year = (it.target as HTMLInputElement).value)
                        )
                    )
                }
            )
            if (props.showFilename) {
                mTextField(
                    label = "Filename",
                    autoFocus = true,
                    margin = MFormControlMargin.normal,
                    type = InputType.text,
                    fullWidth = true,
                    value = props.initialState.location.filename,
                    onChange = {
                        props.onChange(
                            props.initialState.copy(
                                location = props.initialState.location.copy(filename = (it.target as HTMLInputElement).value)
                            )
                        )
                    }
                )
            }
            mTextField(
                label = "Comment",
                autoFocus = true,
                margin = MFormControlMargin.normal,
                type = InputType.text,
                fullWidth = true,
                value = props.initialState.tags.comment,
                onChange = {
                    props.onChange(
                        props.initialState.copy(
                            tags = props.initialState.tags.copy(comment = (it.target as HTMLInputElement).value)
                        )
                    )
                }
            )
        }
        mDialogActions {
            mButton("Cancel", color = MColor.primary, onClick = { props.onClose() }, variant = MButtonVariant.text)
            mButton("Save", color = MColor.primary, onClick = { props.onSave() }, variant = MButtonVariant.text)
        }
    }
}