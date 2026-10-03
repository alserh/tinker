package com.tinker.services

import com.tinker.configurations.Providers
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class LlmGatewayService(
    val providersConfig: Providers
) {

    fun getGeminiClient(): WebClient {
        val googleConfig = providersConfig.providers["google"]
            ?: throw IllegalStateException("Google provider configuration is missing!")

        return WebClient.builder()
            .baseUrl(googleConfig.baseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer ${googleConfig.apiKey}")
            .defaultHeader("x-goog-api-key", googleConfig.apiKey)
            .build()
    }

    fun getOpenRouterClient(): WebClient {
        val openRouterConfig = providersConfig.providers["openrouter"]
            ?: throw IllegalStateException("OpenRouter provider configuration is missing!")

        val client = WebClient.builder()
            .baseUrl(openRouterConfig.baseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer ${openRouterConfig.apiKey}")
            .build()

        return client
    }

}
