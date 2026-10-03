package com.tinker.configurations

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "tinker")
class Providers {
    var providers: Map<String, ProviderConfig> = HashMap()
}

class ProviderConfig {
    lateinit var baseUrl: String
    lateinit var apiKey: String
}