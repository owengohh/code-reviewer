package kt.resources

import jakarta.json.JsonObject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import kt.services.ai.AiStrategy
import kt.services.github.GithubService
import org.slf4j.LoggerFactory
import java.net.http.HttpClient

@Path("/github-webhook")
class GithubHandlerResource(
    private val openAiService: AiStrategy,
    private val githubService: GithubService,
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
            logger.info("PR #$prNumber diff URL: $diffUrl")
            if (diffUrl != null) {
                val diffContent = fetchDiffContent(diffUrl)
                val resp = openAiService.generateCodeReview(diffContent, null)
                val jwt = githubService.generateGithubJWT()
                val token = githubService.getInstallationAccessToken(jwt)
                githubService.writeGeneralComment(
                    pullRequest.getString("issue_url"),
                    "OpenAI generated code review: $resp",
                    token,
                )
            } else {
                logger.warn("Diff URL not found in payload")
            }
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

    private fun fetchDiffContent(diffUrl: String): String {
        val client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build()
        val request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(diffUrl))
            .build()
        val response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    @GET
    @Path("/getToken")
    fun getToken(): Response {
        logger.info("test get token")
        val jwt = githubService.generateGithubJWT()
        val token = githubService.getInstallationAccessToken(jwt)
        return Response.ok(token).build()
    }
}
