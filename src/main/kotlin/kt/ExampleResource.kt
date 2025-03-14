package kt

import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.MediaType
import org.slf4j.LoggerFactory
import java.math.BigDecimal

@Path("/github-webhook")
class ExampleResource {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    fun handleGithubPullRequest(payload: Map<String, Any>) {
        logger.info("Received payload: $payload")
        val action = payload["action"] as String?
        logger.info("Action: ${action ?: "unknown"}")
        if (action == "opened") {
            val prNumber = payload["number"] as BigDecimal?
            val pullRequest = payload["pull_request"] as Map<*, *>?
            val diffUrl = pullRequest?.get("diff_url") as String?
            logger.info("Processing PR # $prNumber - Diff URL: $diffUrl")
        }
    }
}
