package kt.services.github

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import jakarta.enterprise.context.ApplicationScoped
import kt.services.aws.AWSSecretManagerService
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

@ApplicationScoped
class GithubService(
    private val awsSecretManagerService: AWSSecretManagerService,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun writeGeneralComment(issueUrl: String, comment: String, accessToken: String) {
        val requestBody = JsonObject().put("body", comment)
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$issueUrl/comments"))
            .header("Authorization", "Bearer $accessToken")
            .header("Accept", "application/vnd.github.v3+json")
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
            .build()
        val response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString())
        logger.info("GitHub comment response: ${response.body()}")
    }

    fun generateGithubJWT(appId: String = "1179905"): String {
        logger.info("Generating GitHub JWT using private key in AWS Secret Manager")
        val privateKey = readPrivateKey()
        val algorithm = Algorithm.RSA256(null, privateKey)
        val now = Date()
        val exp = Date(now.time + 60000)
        return JWT.create()
            .withIssuer(appId)
            .withIssuedAt(now)
            .withExpiresAt(exp)
            .sign(algorithm)
    }

    fun getInstallationAccessToken(jwt: String): String {
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.github.com/app/installations"))
            .header("Authorization", "Bearer $jwt")
            .header("Accept", "application/vnd.github.v3+json")
            .GET()
            .build()
        val response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString())
        logger.info("GitHub App installations response: ${response.body()}")
        val jsonResponse = JsonArray(response.body())
        val installationId = jsonResponse.getJsonObject(0).getInteger("id")
        logger.info("Installation ID: $installationId")
        val request2 = HttpRequest.newBuilder()
            .uri(URI.create("https://api.github.com/app/installations/$installationId/access_tokens"))
            .header("Authorization", "Bearer $jwt")
            .header("Accept", "application/vnd.github.v3+json")
            .POST(HttpRequest.BodyPublishers.noBody())
            .build()
        val response2 = client.send(request2, java.net.http.HttpResponse.BodyHandlers.ofString())
        logger.info("GitHub App access token response: ${response2.body()}")
        return io.vertx.core.json.JsonObject(response2.body()).getString("token")
    }

    private fun readPrivateKey(): RSAPrivateKey {
        val privateKeyContents = awsSecretManagerService.getSecret("GitHubAppPrivateKey")
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s+".toRegex(), "")
        val keyBytes = Base64.getDecoder().decode(privateKeyContents)
        val spec = PKCS8EncodedKeySpec(keyBytes)
        val kf = KeyFactory.getInstance("RSA")
        return kf.generatePrivate(spec) as RSAPrivateKey
    }
}
