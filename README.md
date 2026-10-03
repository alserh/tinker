# Tinker AI Agent REPL Framework

A lean, high-performance interactive CLI scratchpad built with **Spring Boot 3.4.2** and **Kotlin**. It features custom raw buffer input tracking via a dedicated `CommandLineRunner` loop, providing a robust terminal workspace that smoothly handles multi-line copy-pastes, nested quotes, and complex code blocks directly within your IDE or native Windows CMD.

---

## 🚀 Quick Start

### 1. Configure Your Secrets
The framework safely reads your API credentials directly from your host machine's environment parameters. Set your key before executing:

```cmd
:: Windows CMD
set GEMINI_API_KEY=your_api_key_here

:: PowerShell
$env:GEMINI_API_KEY="your_api_key_here"
```

### 2. Build and Run
Compile the production-ready standalone executable bundle and boot directly into the shell context:

```cmd
gradlew bootJar
java -jar build/libs/tinker-0.0.1-SNAPSHOT.jar
```

### 3. Core Interaction Syntax
Interact with your framework runtime by using the `agent` keyword signature. Pressing `Enter` automatically submits your text payload:

```bash
tinker-shell:> agent Explain dependency injection like I'm 5 years old.
```

---

## 🎯 Project Roadmap

* **Short-Term Goal:** Safely wire up the core reactive `WebClient` pipeline within `GeminiHandler` to stream live token completions from the `gemini-3.6-flash` model endpoint directly back into the console interface.
* **Long-Term Goal:** Build a fully autonomous CLI software engineering agent equipped with a native execution test harness to programmatically review, edit, compile, and manage complex coding tasks throughout your local repository files.
