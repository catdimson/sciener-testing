package news;

import java.time.LocalDate;

public class Log {
    final private int id;
    final private String action;
    final private LocalDate actionTime;
    final private Content content;
    final private User user;

    public Log(int id, String action, LocalDate actionTime, Content content, User user) {
        this.id = id;
        this.action = action;
        this.actionTime = actionTime;
        this.content = content;
        this.user = user;
    }
}
