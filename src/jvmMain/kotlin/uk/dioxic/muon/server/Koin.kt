package uk.dioxic.muon.server

import io.ktor.server.application.*
import io.ktor.util.*
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

internal class Koin(internal val koinApplication: KoinApplication) {
    // Implements ApplicationPlugin as a companion object.
    companion object Plugin : ApplicationPlugin<ApplicationCallPipeline, KoinApplication, Koin> {
        // Creates a unique key for the plugin.
        override val key = AttributeKey<Koin>("Koin")

        // Code to execute when installing the plugin.
        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: KoinApplication.() -> Unit
        ): Koin {
            val koinApplication = startKoin(appDeclaration = configure)
            return Koin(koinApplication)
        }
    }
}