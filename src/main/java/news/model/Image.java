package news.model;

/**
 * Изображение, прикрепляемое к новости
 */
public class Image {
    final private int id;
    private String title;
    private String path;
    private Article article;

    public Image(int id, String title, String path, Article article) {
        this.id = id;
        this.title = title;
        this.path = path;
        this.article = article;
    }

    public void edit(String title, String path, Article article) {
        this.title = title;
        this.path = path;
        this.article = article;
    }
}
