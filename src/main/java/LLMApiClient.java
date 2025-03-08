
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class LLMApiClient {
    private static final String API_ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private static final Duration TIMEOUT = Duration.ofSeconds(60);
    private static final int MAX_RETRIES = 3;

    private static final Logger logger = Logger.getInstance(LLMApiClient.class);
    private final HttpClient client;
    private static final Gson gson = new Gson();

    private static final String API_KEY = "sk-this-is-a-dummy-key-do-not-use";

    public LLMApiClient() {
        this.client = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
    }

    public CompletableFuture<String> analyzeCodeAsync(Project project, String code) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return "{}";
            } catch (Exception e) {
                logger.error("❌ API Analysis Failed: " + e.getMessage(), e);
                return "Analysis failed: " + e.getMessage();
            }
        });
    }

    public String analyzeCode(Project project, String code) throws AnalysisException, ExecutionException, InterruptedException {
        return ApplicationManager.getApplication().executeOnPooledThread(() -> {
            logger.info("🚀 Sending code for analysis...");

            JsonObject payload = new JsonObject();
            payload.addProperty("model", "gpt-4o");
            payload.add("messages", createMessagesArray(code));
            payload.addProperty("temperature", 0.2);

            return executeWithRetry(payload);
        }).get();
    }

    private JsonArray createMessagesArray(String code) {
        JsonArray messages = new JsonArray();

        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content",
                "Return a JSON array of issues found in the code. Only return JSON, nothing else."
        );

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", "Analyze the following Java code and return JSON:\n\n" + code);

        messages.add(systemMessage);
        messages.add(userMessage);
        return messages;
    }

    private String executeWithRetry(JsonObject payload) throws AnalysisException, IOException, InterruptedException {
        logger.info("📤 Sending API Request: " + payload.toString()); // ✅ LOG REQUEST

        HttpRequest request = buildRequest(payload);


        return "{\"issues\": []}";
    }

    private HttpRequest buildRequest(JsonObject payload) {
        return HttpRequest.newBuilder()
                .uri(URI.create(API_ENDPOINT))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .timeout(TIMEOUT)
                .build();
    }

    private String validateResponse(HttpResponse<String> response) throws AnalysisException {
        logger.info("🔍 API Response Code: " + response.statusCode());
        logger.info("📩 Raw API Response: " + response.body());

        if (response.statusCode() != 200) {
            throw new AnalysisException("API Error: " + response.body());
        }

        return parseOpenAIResponse(response.body());
    }

    private String parseOpenAIResponse(String responseBody) {
        try {
            logger.info("✅ OpenAI Raw Response:\n" + responseBody);

            JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray choices = responseJson.getAsJsonArray("choices");

            if (choices != null && choices.size() > 0) {
                return choices.get(0).getAsJsonObject().getAsJsonObject("message").get("content").getAsString();
            }
        } catch (Exception e) {
            logger.error("❌ Failed to parse OpenAI response: " + responseBody, e);
            return responseBody;
        }

        return "⚠️ Unexpected response format.";
    }

    private void waitForRetry(int attempt) {
        try {
            Thread.sleep((long) (Math.pow(2, attempt) * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
