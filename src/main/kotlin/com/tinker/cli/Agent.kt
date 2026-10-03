package com.tinker.cli

import org.springframework.stereotype.Component

/**
 * The chat side of the REPL: one message in, one reply out.
 *
 * Today it echoes the payload so the input path stays verifiable at a glance.
 * This is the seam where the model call belongs - GeminiHandler through
 * LlmGatewayService - and later where tool selection will live.
 *
 * message arrives verbatim: real newlines and indentation survive, because
 * nothing between the console and here re-tokenises it.
 */
@Component
class Agent {

    fun respond(message: String): String = buildString {
        appendLine("Captured raw block: ${message.length} characters")
        appendLine("System processed your input cleanly:")
        appendLine("----------------")
        append(message.trimEnd('\n'))
    }
}