package kt.services.aws

import jakarta.enterprise.context.ApplicationScoped
import org.slf4j.LoggerFactory
import software.amazon.awssdk.http.apache.ApacheHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient

@ApplicationScoped
class AWSSecretManagerService {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val secretManagerClient = SecretsManagerClient.builder().httpClient(ApacheHttpClient.builder().build()).region(Region.AP_SOUTHEAST_1).build()

    fun getSecret(secretName: String): String {
        logger.info("Fetching secret $secretName from AWS Secret Manager")
        val secretValue = secretManagerClient.getSecretValue { it.secretId(secretName) }.secretString()
        logger.info("Secret $secretName fetched successfully")
        return secretValue
    }
}
