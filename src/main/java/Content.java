public class Content {
    int id;
    String entity;

    Content(int id, String title) {
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