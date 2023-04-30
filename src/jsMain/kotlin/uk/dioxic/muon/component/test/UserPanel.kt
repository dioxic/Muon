package uk.dioxic.muon.component.test

import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.useContext
import uk.dioxic.muon.hook.test.useDeleteUser
import uk.dioxic.muon.hook.test.useUpdateUser
import web.cssom.BorderCollapse
import web.cssom.LineStyle
import web.cssom.WhiteSpace
import web.cssom.px

typealias UserPanelProps = Props

val UserPanel = FC<UserPanelProps> {
    val selectedUser = useContext(SelectedUserContext)
    val updateUser = useUpdateUser()
    val deleteUser = useDeleteUser()

    div {
        div {
            +"User Information"

            button {
                css {
                    marginLeft = 20.px
                }
                disabled = selectedUser == null
                onClick = { updateUser(selectedUser!!.apply { name += "_Updated" }) }

                +"Update"
            }

            button {
                css {
                    marginLeft = 20.px
                }
                disabled = selectedUser == null
                onClick = { deleteUser(selectedUser!!) }

                +"Delete"
            }
        }

        div {
            css {
                width = 400.px
                borderSpacing = 0.px
                borderCollapse = BorderCollapse.collapse
                whiteSpace = WhiteSpace.nowrap
                borderWidth = 2.px
                borderStyle = LineStyle.solid
                borderColor = Colors.Stroke.Gray
                marginTop = 20.px
            }

            div {
                +"Name: ${selectedUser?.name ?: "—"}"
            }

            div {
                +"E-mail: ${selectedUser?.email ?: "—"}"
            }

            div {
                +"Phone: ${selectedUser?.phone ?: "—"}"
            }

            div {
                +"Website: ${selectedUser?.website ?: "—"}"
            }
        }
    }
}
