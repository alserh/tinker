package com.tinker.configurations

import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Owns the terminal and the line editor.
 *
 * This file used to hook a custom org.jline.reader.Parser to guess at paste
 * markers. That cannot work at that layer: the parser receives the assembled
 * line, not the raw escape sequences, and throwing EOFError means "ask me for
 * another line" - which is what made the shell sit there indefinitely.
 *
 * Paste handling now lives in AgentReplLauncher, above the terminal, so it
 * works on consoles that never emit paste markers at all.
 */
@Configuration
class TerminalConfig {

    @Bean(destroyMethod = "close")
    fun terminal(): Terminal = TerminalBuilder.builder()
        .system(true)
        .build()

    @Bean
    fun customLineReader(terminal: Terminal): LineReader = LineReaderBuilder.builder()
        .terminal(terminal)
        .build()
        .apply {
            // Honoured by terminals that support bracketed paste; harmless where
            // they do not - the legacy Windows console ignores the request.
            setOpt(LineReader.Option.BRACKETED_PASTE)
            setVariable(LineReader.LIST_MAX, 1000)
        }
}