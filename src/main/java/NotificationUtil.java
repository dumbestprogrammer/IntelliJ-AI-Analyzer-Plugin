import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class NotificationUtil {
    private static final NotificationGroup NOTIFICATION_GROUP =
            NotificationGroupManager.getInstance().getNotificationGroup("ai_code_analysis");

    public static void showInfo(Project project, String title, String message) {
        NOTIFICATION_GROUP.createNotification(title, message, NotificationType.INFORMATION)
                .notify(project);
    }

    public static void showError(Project project, String title, String message) {
        NOTIFICATION_GROUP.createNotification(title, message, NotificationType.ERROR)
                .notify(project);
    }
}
