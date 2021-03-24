package news;

import java.util.List;

public class Content {
    final private int id;
    private String entity;
    private List<Log> logs;

    public Content(int id, String title, List<Log> logs) {
        this.id = id;
        this.entity = title;
        this.logs = logs;
    }

    public void changeEntity(String newEntity) {
        this.entity = newEntity;
    }

    public String getEntity() {
        return this.entity;
    }
}