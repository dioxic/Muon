package uk.dioxic.muon.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import uk.dioxic.muon.cli.command.Rename

class Cli : CliktCommand() {
    init {
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager")
    }

    override fun run() = Unit
}

fun main(args: Array<String>) = Cli()
    .subcommands(Rename())
    .main(args)