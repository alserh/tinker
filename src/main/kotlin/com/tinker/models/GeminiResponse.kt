package com.tinker.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiResponse(
    val id: String,
    val model: String,
    val usage: Any,
    val steps: List<SimpleStep> = emptyList()
) {
    // Easy visual helper to extract the message text directly
    val textContent: String?
        get() = steps.firstOrNull { it.type == "model_output" }?.content?.firstOrNull()?.text
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class SimpleStep(
    val type: String, // "thought" or "model_output"
    val content: List<SimpleContent>?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SimpleContent(
    val text: String
)
