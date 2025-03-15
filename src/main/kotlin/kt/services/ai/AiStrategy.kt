package kt.services.ai

interface AiStrategy {
    fun generateCodeReview(diffContent: String, history: List<String>?): String
}
