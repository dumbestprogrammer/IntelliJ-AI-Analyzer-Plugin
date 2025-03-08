import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

public class Issue {
    private final int line;
    private final String type;
    private final String description;
    private final String suggestion;
    private final @Nullable VirtualFile file;

    public Issue(int line, String type, String description, String suggestion, @Nullable VirtualFile file) {
        this.line = line;
        this.type = type;
        this.description = description;
        this.suggestion = suggestion;
        this.file = file;
    }

    public int getLine() {
        return line;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public @Nullable VirtualFile getFile() {
        return file;
    }

    @Override
    public String toString() {
        String fileName = (file != null) ? file.getName() : "Unknown File";
        return type + " (Line " + line + ") in " + fileName + ": " + description;
    }
}