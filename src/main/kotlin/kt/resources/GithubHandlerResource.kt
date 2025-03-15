package kt.resources

import jakarta.json.JsonObject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import kt.services.ai.AiStrategy
import org.slf4j.LoggerFactory

@Path("/github-webhook")
class GithubHandlerResource(
    private val openAiService: AiStrategy,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @GET
    @Path("/healthcheck")
    fun hello(): Response {
        return Response.ok("healthy!").build()
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    fun handleGithubPullRequest(payload: JsonObject) {
        logger.info("Received payload: $payload")
        val action = payload.getString("action", null)
        logger.info("Action: ${action ?: "unknown"}")
        if (action == "opened") {
            val prNumber = payload.getJsonNumber("number")?.bigDecimalValue()
            val pullRequest = payload.getJsonObject("pull_request")
            val diffUrl = pullRequest?.getString("diff_url", null)
            logger.info("Processing PR # $prNumber - Diff URL: $diffUrl")
        }
    }

    @POST
    @Path("/openai")
    @Consumes(MediaType.APPLICATION_JSON)
    fun testOpenAI(payload: JsonObject) {
        logger.info("Received payload: $payload")
        val diffContent = payload.getString("diff_content", null)
        val resp = openAiService.generateCodeReview(diffContent, null)
        logger.info("OpenAI response: $resp")
    }
}
