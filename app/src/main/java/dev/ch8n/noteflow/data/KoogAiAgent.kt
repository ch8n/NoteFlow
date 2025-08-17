package dev.ch8n.noteflow.data

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.llms.all.simpleOpenRouterExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel

fun initKoogAiAgent(
    apiKey: String,
    modelName: String,
    systemPrompt: String = "You are a helpful assistant.",
): AIAgent<String, String> {
    return AIAgent(
        executor = simpleOpenRouterExecutor(apiKey),
        systemPrompt = systemPrompt,
        llmModel = LLModel(
            provider = LLMProvider.OpenRouter,
            id = modelName,
            capabilities = listOf(
                LLMCapability.Temperature,
                LLMCapability.Schema.JSON.Full,
                LLMCapability.Speculation,
                LLMCapability.Tools,
                LLMCapability.ToolChoice,
                LLMCapability.Completion
            )
        )
    )
}
