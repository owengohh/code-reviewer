package kt

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler

class ExampleResource: RequestHandler<String, String> {
    override fun handleRequest(p0: String, p1: Context?): String {
        return "Hello from Quarkus REST"
    }
}
