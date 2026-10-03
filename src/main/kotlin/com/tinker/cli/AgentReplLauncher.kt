package com.tinker.cli

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.util.Scanner

@Component
class AgentReplLauncher(
    private val agentCommands: AgentCliCommands
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        // Spawns immediately after Spring Boot successfully boots up
        println("\n🤖 ===================================================")
        println("🚀 [Tinker AI Agent REPL Framework Active]")
        println("-> Type: agent <your prompt payload here>")
        println("-> Type 'exit' or 'quit' to close the framework engine.")
        println("======================================================")

        val scanner = Scanner(System.`in`)

        while (true) {
            print("\ntinker-shell:> ")
            System.out.flush()

            if (!scanner.hasNextLine()) break
            val line = scanner.nextLine().trim()

            if (line.lowercase() == "exit" || line.lowercase() == "quit") {
                println("Shutting down agent framework runtime context...")
                break
            }

            if (line.startsWith("agent ")) {
                val promptPayload = line.removePrefix("agent ").trim()

                // Safely slices your text words into an array, neutralizing inner quote execution bugs!
                val tokens = promptPayload.split("\\s+".toRegex()).toTypedArray()

                // Routes the token array directly to your existing command class method
                val result = agentCommands.talk(tokens)
                println(result)
            } else if (line.isNotEmpty()) {
                println("❌ Command not recognized. Your input sequence must start with the 'agent ' keyword.")
            }
        }
    }
}
