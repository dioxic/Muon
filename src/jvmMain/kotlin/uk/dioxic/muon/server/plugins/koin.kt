package uk.dioxic.muon.server.plugins

import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

val KoinPlugin = createApplicationPlugin(
    name = "Koin",
    createConfiguration = ::KoinConfig
) {
    on(MonitoringEvent(ApplicationStarted)) {
        startKoin(appDeclaration = pluginConfig.appDeclaration)
    }
}

class KoinConfig {
    var appDeclaration: KoinAppDeclaration = {}
}