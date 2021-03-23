package domain;

public class Image {
    final private int id;
    private String title;
    private String path;
    private New article;

    public Image(int id, String title, String path, New article) {
        this.id = id;
        this.title = title;
        this.path = path;
        this.article = article;
    }

    public void edit(String title, String path, New article) {
        this.title = title;
        this.path = path;
        this.article = article;
    }
}
