package kt.resources

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.jupiter.api.Test

@QuarkusTest
class GithubHandlerResourceTest {

    @Test
    fun testHandleGithubPullRequest() {
        val payload = buildJsonObject {
            put("action", "opened")
        }.toString()

        given()
            .contentType("application/json")
            .body(payload)
            .`when`()
            .post("/github-webhook")
            .then()
            .statusCode(204) // Assuming no content response
    }

    @Test
    fun healthcheck() {
        given()
            .`when`()
            .get("/github-webhook/healthcheck")
            .then()
            .statusCode(200)
    }
}
