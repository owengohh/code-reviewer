package kt.services.ai

import dev.langchain4j.model.openai.OpenAiChatModel
import jakarta.inject.Singleton
import kt.config.AppConfig
import org.slf4j.LoggerFactory

@Singleton
class OpenAiService(
    appConfig: AppConfig,
) : AiStrategy {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val openAiModel = OpenAiChatModel.builder()
        .apiKey(appConfig.openAiApiKey)
        .modelName("gpt-4-turbo")
        .temperature(0.1)
        .responseFormat("json")
        .strictJsonSchema(true)
        .build()

    override fun generateCodeReview(diffContent: String, history: List<String>?): String {
        val limitedHistory = history?.takeLast(3)?.joinToString("\n")
        val trimmedDiff = diffContent.lines().joinToString("\n") { line ->
            if (line.startsWith("+") || line.startsWith("-")) line else ""
        }
        logger.info("Generating code review using gpt-4-turbo model")
        val prompt = """
        You are an AI-powered code reviewer.
         **Recent Review History**:
        ${limitedHistory?.ifEmpty { "No prior feedback available." }}
         **Modified Code (Only Key Changes)**:
        ```
        $trimmedDiff
        ```
        Provide **concise, actionable feedback** focusing on:
        - Code correctness
        - Best practices
        - Performance & security improvements
        - Readability

        Respond in **GitHub Markdown format** for clear presentation.
        """.trimIndent()
        val response = openAiModel.chat(prompt)
        logger.info("OpenAI response: $response")
        return response
    }
}
