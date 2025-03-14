package kt

import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.MediaType
import kotlinx.serialization.json.JsonObject
import org.slf4j.LoggerFactory

@Path("/github-webhook")
class ExampleResource {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    fun handleGithubPullRequest(payload: Map<String, Any>) {
        logger.info("Received payload: $payload")
        val action = payload.getOrDefault("action", null)
        if (action != null) {
            logger.info("Action: $action")
        }
    }
}
