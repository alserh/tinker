package com.tinker.config

import org.jline.reader.EOFError
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.reader.Parser
import org.jline.terminal.Terminal
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy

@Configuration
class TerminalConfig {

    @Bean
    fun customLineReader(
        @Lazy terminal: Terminal
    ): LineReader {
        // Create an explicit parser interceptor that checks for text-paste status blocks
        val pasteAwareParser = Parser { line, cursor, context ->
            // Check if the current terminal input stream is inside a bracketed paste event block
            if (line.contains("\u001B[200~") && !line.contains("\u001B[201~")) {
                // Throw an EOF exception to instruct JLine that the text block is incomplete; keep collecting text!
                throw EOFError(-1, -1, "Pasting text block in progress...")
            }

            // Otherwise, process the string input natively using standard JLine token mapping mechanics
            org.jline.reader.impl.DefaultParser().parse(line, cursor, context)
        }

        val lineReader = LineReaderBuilder.builder()
            .terminal(terminal)
            .parser(pasteAwareParser) // Intercept character streams natively
            .build()

        // Turn on terminal option level processing parameters
        lineReader.setOpt(LineReader.Option.BRACKETED_PASTE)
        lineReader.setVariable(LineReader.LIST_MAX, 1000)

        return lineReader
    }
}
