package com.tinker.cli

import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption

@ShellComponent
class AgentCliCommands {

    @ShellMethod(key = ["agent"], value = "Talk to your AI agent framework automatically.")
    fun talk(
        // In Spring Shell 3.x, passing an empty string array directly to 'value'
        // turns this parameter into an unnamed, greedy positional collector.
        @ShellOption(value = [""], defaultValue = "Hello Agent", help = "The raw prompt message string")
        message: Array<String>
    ): String {
        // Stitch the array elements back together with single spaces
        val joinedMessage = message.joinToString(" ")

        println("\n📥 [Captured raw block: ${joinedMessage.length} characters successfully]")

        return "System processed your input cleanly:\n----------------\n$joinedMessage"
    }
}
