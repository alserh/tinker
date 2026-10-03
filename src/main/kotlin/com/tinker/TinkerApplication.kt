package com.tinker

import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@SpringBootApplication
class TinkerApplication

fun main(args: Array<String>) {
	SpringApplicationBuilder(TinkerApplication::class.java)
		.web(WebApplicationType.NONE)
		.run(*args)
}
