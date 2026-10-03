package com.tinker.cli

import org.jline.reader.EndOfFileException
import org.jline.reader.LineReader
import org.jline.reader.UserInterruptException
import org.jline.utils.NonBlockingReader
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.IOException

/**
 * The one and only input loop.
 *
 * A pasted block reaches the agent as a single message by either of two routes:
 *
 *  1. On a terminal that supports bracketed paste (Windows Terminal, WezTerm,
 *     mintty), JLine folds the whole paste into one line for us.
 *  2. Everywhere else - including the legacy Windows console, which cannot emit
 *     paste markers - pasted lines arrive back-to-back with no gap, so we keep
 *     collecting while characters are already queued. A typed line stays one
 *     message, because nothing is queued when a human stops to think.
 *
 * No sentinel, no 'agent' prefix, no script files: just type, or just paste.
 */
@Component
class AgentReplLauncher(
    private val lineReader: LineReader,
    private val agent: Agent
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        printBanner()

        while (true) {
            val message = readMessage() ?: break
            val text = message.trim()

            if (text.isEmpty()) continue

            if (text.startsWith("/")) {
                if (!runCommand(text)) break
                continue
            }

            println(agent.respond(message))
        }

        println("Shutting down agent framework runtime context...")
    }

    /**
     * Returns one logical message, or null at end of input.
     */
    private fun readMessage(): String? {
        val first = readLineOrNull(PROMPT) ?: return null
        val block = StringBuilder(first)
        var insideBurst = false

        while (true) {
            // Once a paste is in progress, tolerate a longer silence before
            // declaring it finished.
            val waitMs = if (insideBurst) BURST_IDLE_MS else FIRST_GAP_MS
            if (!inputWaiting(waitMs)) break

            val next = readLineOrNull(CONTINUATION_PROMPT) ?: break
            block.append('\n').append(next)
            insideBurst = true
        }

        // A pasted block usually carries its own trailing newline; drop it.
        return block.toString().trimEnd('\n')
    }

    private fun readLineOrNull(prompt: String): String? =
        try {
            lineReader.readLine(prompt)
        } catch (e: UserInterruptException) {
            // Ctrl-C abandons the current line instead of killing the REPL.
            ""
        } catch (e: EndOfFileException) {
            // Ctrl-D, or exhausted piped input.
            null
        }

    /**
     * True when more characters are already waiting, i.e. we are mid-paste.
     * peek() does not consume, so the line reader still sees the same bytes.
     */
    private fun inputWaiting(timeoutMs: Long): Boolean =
        try {
            lineReader.terminal.reader().peek(timeoutMs) != NonBlockingReader.READ_EXPIRED
        } catch (e: IOException) {
            false
        }

    private fun runCommand(command: String): Boolean = when (command.lowercase()) {
        "/quit", "/exit", "/q" -> false
        "/clear" -> {
            print("\u001B[2J\u001B[H")
            System.out.flush()
            true
        }
        "/help", "/?" -> {
            println(
                """
                Commands
                  /help          show this
                  /clear         clear the screen
                  /quit, /exit   leave the REPL
                Everything else goes to the agent - no 'agent' prefix needed.
                Paste freely: a pasted block arrives as one message.
                """.trimIndent()
            )
            true
        }
        else -> {
            println("Unknown command '$command' - try /help")
            true
        }
    }

    private fun printBanner() {
        println(
            """
            ===================================================
            [Tinker AI Agent REPL Framework Active]
            -> Type anything to talk to the agent. No prefix needed.
            -> Paste code freely: a pasted block arrives as one message.
            -> /help for commands, /quit to exit.
            ===================================================
            """.trimIndent()
        )
    }

    private companion object {
        const val PROMPT = "tinker> "

        /** Empty on purpose: a paste should not be peppered with prompts. */
        const val CONTINUATION_PROMPT = ""

        /** Silence after a typed line before we accept it as standalone. */
        const val FIRST_GAP_MS = 60L

        /** Silence tolerated inside a paste before the block is complete. */
        const val BURST_IDLE_MS = 300L
    }
}