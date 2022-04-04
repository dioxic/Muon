package uk.dioxic.muon.component.table.plugin

import kotlinx.js.jso
import mui.material.Checkbox
import mui.material.CheckboxColor
import react.FC
import react.createElement
import react.table.PluginHook
import react.table.SimpleColumn
import react.table.TableToggleAllRowsSelectedProps
import react.table.TableToggleRowsSelectedProps
import uk.dioxic.muon.component.table.CellType

val useCheckboxSelect = PluginHook<Any> {
    val newColumn = jso<SimpleColumn<Any, *>> {
        id = CellType.CHECKBOX.id
        headerFunction = { header ->
            val component = FC<TableToggleAllRowsSelectedProps> { props ->
                Checkbox {
                    color = CheckboxColor.primary
                    indeterminate = props.indeterminate
                    checked = props.checked
                    onChange = { event, _ -> props.onChange(event) }
                }
            }
            createElement(component, header.getToggleAllRowsSelectedProps())
        }
        cellFunction = { cell ->
            val component = FC<TableToggleRowsSelectedProps> { props ->
                Checkbox {
                    color = CheckboxColor.primary
                    checked = props.checked
                    onChange = { event, _ -> props.onChange(event) }
                }
            }
            createElement(component, cell.row.getToggleRowSelectedProps())
        }
    }

    allColumns += { columns, _ ->
        arrayOf(newColumn, *columns)
    }
}