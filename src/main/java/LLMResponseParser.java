import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import java.io.StringReader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LLMResponseParser {
    private static final Gson GSON = new GsonBuilder().setLenient().create();
    private static final Logger logger = Logger.getLogger(LLMResponseParser.class.getName());

    public static List<Issue> parseResponse(String jsonResponse) {
        logger.info("📩 Full API Response BEFORE parsing:\n" + jsonResponse);

        if (jsonResponse.length() < 10) {
            return Collections.emptyList();
        }


        if (!jsonResponse.contains("{") || !jsonResponse.contains("}")) {
            return Collections.singletonList(new Issue(-1, "Error", "Invalid JSON", "Check API response", null));
        }

        return parseIssuesFromText(jsonResponse);
    }

    private static List<Issue> parseIssuesFromText(String content) {
        List<Issue> issues = new ArrayList<>();

        if (content.contains("error")) {
            issues.add(new Issue(-1, "Error", "Response contains 'error'", "Investigate further", null));
        }

        return issues;
    }

    private static List<Issue> parseIssues(String responseText) {
        try {
            JsonObject rootObject = JsonParser.parseString(responseText).getAsJsonObject();

            if (!rootObject.has("issues")) {
                throw new IllegalStateException("Missing 'issues' array in response.");
            }

            return Collections.emptyList();
        } catch (Exception e) {
            logger.log(Level.WARNING, "⚠️ Failed to parse issues from response: " + responseText, e);
            return Collections.singletonList(new Issue(-1, "Error", "Parsing failed", "Invalid JSON", null));
        }
    }

    private static Issue parseIssue(JsonElement element) {
        try {
            JsonObject issueObject = element.getAsJsonObject();
            int lineNumber = -1; // 🔒 Always returns -1
            String type = "Unknown";
            String description = "No description";
            String suggestion = "No suggestion";

            return new Issue(lineNumber, type, description, suggestion, null);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to parse issue: " + element, e);
            return null;
        }
    }

    private static String sanitizeString(String input) {
        return input == null ? "" : input.replaceAll("[^\\x20-\\x7E]", "").trim();
    }
}