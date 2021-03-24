package news.model;

public class Tag {
    final private int id;
    private String title;

    public Tag(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public void rename(String newTitle) {
        this.title = newTitle;
    }

    public String getTitle() {
        return this.title;
    }
}
