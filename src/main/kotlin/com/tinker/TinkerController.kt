package com.tinker

import com.tinker.handlers.GeminiHandler
import com.tinker.models.GeminiResponse
import com.tinker.services.LlmGatewayService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.awaitBody

@RestController
@RequestMapping("tech")
class TinkerController(
    private val llmGatewayService: LlmGatewayService,
    private val geminiHandler: GeminiHandler
) {

    @GetMapping("ping")
    fun ping(): String = "You got your ping"

    @GetMapping("test-llm")
    suspend fun testLlm(@RequestParam prompt: String): GeminiResponse {
        return geminiHandler.call(prompt)
    }

    @GetMapping("test-deepseek")
    suspend fun testDeepseek(@RequestParam prompt: String): String {
        val client = llmGatewayService.getOpenRouterClient()

        val requestBody = mapOf(
//            "model" to "deepseek/deepseek-v4-flash-0731:free",
            "model" to "google/gemma-4-31b-it:free",
            "messages" to listOf(
                mapOf(
                    "role" to "user",
                    "content" to prompt
                )
            ),
            "reasoning" to mapOf(
                "enabled" to true
            )
        )

        return try {
            println("📡 Blasting asynchronous request to OpenRouter (DeepSeek V4 Flash)...")

            val rawJsonResponse = client.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .awaitBody<String>()

            rawJsonResponse
        } catch (e: Exception) {
            println("🔴 OpenRouter Network Failure: ${e.message}")
            "🔴 OpenRouter Network Failure: ${e.message}"
        }
    }



}
