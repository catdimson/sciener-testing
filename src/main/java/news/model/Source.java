package news.model;

/**
 * Источник для новостей и афиш
 */
public class Source {
    private int id;
    private String title;
    private String url;

    public Source(int id, String title, String url) {
        this.id = id;
        this.url = url;
        this.title = title;
    }

    public Source(String title, String url) {
        this.title = title;
        this.url = url;
    }

    /**
     * Редактирование источника
     */
    public void editSource(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public boolean equalsWithId(int id) {
        return this.id == id;
    }

    public boolean equalsWithTitle(String title) {
        return this.title.equals(title);
    }

    public Object[] getObjects() {
        return new Object[] {
                id,
                title,
                url
        };
    }

    public static String[] getFields() {
        return new String[] {
                "id", "title", "url"
        };
    }
}
