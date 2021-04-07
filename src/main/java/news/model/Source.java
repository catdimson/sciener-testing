package news.model;

/**
 * Источник для новостей и афиш
 */
public class Source {
    private int id;
    private String title;
    private String url;

    public Source(int id, String url, String title) {
        this.id = id;
        this.url = url;
        this.title = title;
    }

    public Source(String url, String title) {
        this.url = url;
        this.title = title;
    }

    /**
     * Редактирование источника
     */
    public void editSource(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public String getSourceUrl() {
        return this.url;
    }
}
