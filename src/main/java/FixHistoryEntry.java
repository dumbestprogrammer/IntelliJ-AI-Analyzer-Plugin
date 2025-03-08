import java.io.File;
import java.time.Instant;
import java.util.List;

public class FixHistoryEntry {
    public final Instant timestamp;
    public final List<File> backups;
    public final List<String> modifiedFiles;

    public FixHistoryEntry(Instant timestamp, List<File> backups, List<String> modifiedFiles) {
        this.timestamp = timestamp;
        this.backups = backups;
        this.modifiedFiles = modifiedFiles;
    }
}