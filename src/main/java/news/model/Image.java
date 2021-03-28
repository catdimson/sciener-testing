package news.model;

public class Image {
    final private int id;
    private String title;
    private String path;
    private Articles article;

    public Image(int id, String title, String path, Articles article) {
        this.id = id;
        this.title = title;
        this.path = path;
        this.article = article;
    }

    public void edit(String title, String path, Articles article) {
        this.title = title;
        this.path = path;
        this.article = article;
    }
}
