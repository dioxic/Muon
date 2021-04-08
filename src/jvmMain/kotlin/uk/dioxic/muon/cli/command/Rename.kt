package uk.dioxic.muon.cli.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import org.jaudiotagger.audio.AudioFileIO
import uk.dioxic.muon.MusicFileJvm
import uk.dioxic.muon.isAudioFile
import java.util.logging.Level

class Rename : CliktCommand(help = "Rename music files") {
    init {
        context { helpFormatter = CliktHelpFormatter(showDefaultValues = true) }
    }

    private val dir by option("-d", "--dir", help = "Source directory")
        .file(mustExist = true)
        .required()
    private val recursive by option("-r", "--recursive", help = "Traverse directory recursively")
        .flag()

    override fun run() {

        AudioFileIO.logger.level = Level.OFF

        if (dir.isFile) {
            sequenceOf(dir)
        } else if (recursive) {
            dir.walk().asSequence()
        } else {
            dir.listFiles()!!.asSequence()
        }.filter { it.isAudioFile }
            .map { MusicFileJvm(it) }
            .forEach { println("${it.file.name} -> ${it.targetFilename}") }
    }
}