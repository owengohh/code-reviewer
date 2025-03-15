package kt.config

import jakarta.annotation.PostConstruct
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.slf4j.LoggerFactory

@ApplicationScoped
class AppConfig {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @ConfigProperty(name = "quarkus_langchain4j_openai_api_key", defaultValue = "dummy")
    lateinit var openAiApiKey: String

    @PostConstruct
    fun onInit() {
        logger.info("OpenAI API Key: $openAiApiKey")
    }
}
