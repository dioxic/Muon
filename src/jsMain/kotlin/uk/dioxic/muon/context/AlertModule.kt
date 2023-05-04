package uk.dioxic.muon.context

import js.core.jso
import mui.material.*
import mui.system.sx
import react.FC
import react.PropsWithChildren
import react.createContext
import react.useState
import web.cssom.integer

val AlertContext = createContext<AlertContextDto>()

sealed class Alert(val severity: AlertColor, val message: String) {
    class AlertError(message: String) : Alert(AlertColor.error, message)
    class AlertWarning(message: String) : Alert(AlertColor.warning, message)
    class AlertInfo(message: String) : Alert(AlertColor.info, message)
    class AlertSuccess(message: String) : Alert(AlertColor.success, message)
}

data class AlertContextDto(
    val alerts: List<Alert>,
    val addAlert: (Alert) -> Unit,
)

val AlertModule = FC<PropsWithChildren> { props ->
    val (alerts, setAlerts) = useState<List<Alert>>(emptyList())
    val (isOpen, setIsOpen) = useState(false)

    fun handleAlertClose() {
        setIsOpen(false)
    }

    fun handleAddAlert(alert: Alert) {
        setAlerts(alerts + alert)
        setIsOpen(true)
    }

    AlertContext.Provider(
        AlertContextDto(
            alerts = alerts,
            addAlert = ::handleAddAlert
        )
    ) {
        Snackbar {
            open = isOpen
            onClose = { _, reason ->
                if (reason != SnackbarCloseReason.clickaway) {
                    handleAlertClose()
                }
            }
            autoHideDuration = 6000
            anchorOrigin = jso {
                horizontal = SnackbarOriginHorizontal.center
                vertical = SnackbarOriginVertical.top
            }
            sx {
                zIndex = integer(2_000)
            }

            Alert {
                severity = alerts.lastOrNull()?.severity
                onClose = { _ -> handleAlertClose() }

                +"${alerts.lastOrNull()?.message}"
            }
        }

        +props.children
    }
}