package uk.dioxic.muon.component.table

import csstype.Background
import csstype.Flex
import csstype.px
import csstype.rgba
import kotlinx.js.jso
import mui.material.InputBase
import mui.material.InputBaseMargin
import mui.material.Mui
import mui.material.MuiOutlinedInput
import mui.system.sx
import react.FC
import react.Props
import react.ReactNode
import react.create
import react.table.CellProps
import react.table.SimpleColumn

fun <D : Any> column(block: SimpleColumn<D, *>.() -> Unit) =
    jso<SimpleColumn<D, *>>().apply(block)

external interface EditableCellProps : Props {
    var value: Any
    var rowIndex: Int
    var columnId: Int
    var handleUpdate: (Int, Int, Any) -> Unit
}

fun <D : Any, V> editableColumn(block: SimpleColumn<D, V>.() -> Unit) {
    val column = jso<SimpleColumn<D, V>> {
        // Convert Kotlin objects to String otherwise react-table thinks they are React Components
        cellFunction = { props -> props.value.toString().unsafeCast<ReactNode>() }
        block()
    }
}

//val CssTextField = styled(Input, jso {
//    something = "sdf"
//})


fun <D : Any, V> editableCell(cellProps: CellProps<D, V>) =
    FC<Props> {
        InputBase {
            sx {
                padding = 0.px
                border = 0.px
//                flex = Flex.fitContent
                MuiOutlinedInput.root {
                    padding = 0.px
                    borderRadius = 0.px
                    flex = Flex.fitContent
                }

                Mui.focused
                "&.Mui-focused" {
                    outline = 1.px
                    outlineColor = rgba(0,0,0,0.08)
                }
                background = "transparent".asDynamic().unsafeCast<Background>()
            }
//            onClick = {event -> event.preventDefault()}
//            readOnly = true
//            variant = FormControlVariant.outlined


//            onDoubleClick = { _ ->
//                println("dbl click")
//                this.readOnly = false
//            }
//            onBlur = { _ -> this.readOnly = true }
            defaultValue = cellProps.value
            multiline = true
            margin = InputBaseMargin.none
//            fullWidth = true

            +cellProps.cell.value.toString()
        }
    }.create()

fun <D : Any> editableColumn() {
    column<D> {
        cellFunction = jso {

        }
    }
}