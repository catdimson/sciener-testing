import java.util.Date;

public class Log {
    int id;
    String action;
    Date actionTime;
    Content content;
    User user;

    Log(int id, String action, Date actionTime, Content content, User user) {
        this.id = id;
        this.action = action;
        this.actionTime = actionTime;
        this.content = content;
        this.user = user;
    }
}
