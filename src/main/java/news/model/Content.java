package news.model;

public class Content {
    final private int id;
    private String entity;

    public Content(int id, String title) {
        this.id = id;
        this.entity = title;
    }

    public void changeEntity(String newEntity) {
        this.entity = newEntity;
    }

    public String getEntity() {
        return this.entity;
    }
}