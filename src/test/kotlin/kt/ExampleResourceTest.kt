package kt

import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Test
import com.amazonaws.services.lambda.runtime.Context
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat

@QuarkusTest
class ExampleResourceTest {
    @Test
    fun testHandleRequest() {
        val exampleResource = ExampleResource()
        val context = mockk<Context>()
        val result = exampleResource.handleRequest("test input", context)
        assertThat(result).isEqualTo("Hello from Quarkus REST")
    }
}
