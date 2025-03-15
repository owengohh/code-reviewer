package kt.config

import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty

@ApplicationScoped
class AppConfig {

    @ConfigProperty(name = "quarkus_langchain4j_openai_api_key", defaultValue = "dummy")
    lateinit var openAiApiKey: String
}
