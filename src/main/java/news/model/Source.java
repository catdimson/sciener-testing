package news.model;

/**
 * Источник для новостей и афиш
 */
public class Source {
    private int id;
    private String url;

    public Source(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public Source(String url) {
        this.url = url;
    }

    /**
     * Редактирование источника
     */
    public void editSource(String url) {
        this.url = url;
    }

    public String getSourceUrl() {
        return this.url;
    }
}
