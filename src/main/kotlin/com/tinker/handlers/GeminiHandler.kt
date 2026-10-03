package com.tinker.handlers

import com.tinker.models.GeminiResponse
import com.tinker.models.SimpleContent
import com.tinker.models.SimpleStep
import com.tinker.services.LlmGatewayService
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.awaitBody
import com.fasterxml.jackson.databind.ObjectMapper

@Service
class GeminiHandler(
    private val llmGatewayService: LlmGatewayService,
    private val objectMapper: ObjectMapper
) {
    val client = llmGatewayService.getGeminiClient()



    suspend fun call(prompt: String): GeminiResponse {
        val requestBody = mapOf(
//            "model" to "gemini-3.8-flash",
//            "model" to "gemini-3.5-flash-lite",
            "model" to "gemini-3.6-flash",
//            "model" to "gemini-2.5-flash",
            "input" to prompt
        )

        val jsonStringPayload: String = objectMapper.writeValueAsString(requestBody)

        return try {
            val rawJsonResponse = client.post()
                .uri("/v1beta/interactions")
                .bodyValue(jsonStringPayload)
                .retrieve()
                .awaitBody<GeminiResponse>()

            rawJsonResponse
        } catch (e: org.springframework.web.reactive.function.client.WebClientResponseException.TooManyRequests) {
            println("\n🛑 [429] Rate limit hit! Take a breath, wait 60 seconds, and try again.")
            // Return an empty/dummy GeminiResponse containing the warning message so the CLI doesn't explode
            GeminiResponse(
                id = "RATE_LIMIT_BLOCKED",
                model = "gemini-3.8-flash",
                usage = "null",
                steps = listOf(SimpleStep("System is cooling down... Wait a minute.", null))
            )
        } catch (e: Exception) {
            println("🔴 General Interaction Error: ${e.message}")
            throw RuntimeException("Gemini communication failed", e)
        }
    }
}