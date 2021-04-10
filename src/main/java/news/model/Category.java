package news.model;

/**
 * Категория новости
 */
public class Category {
    private int id;
    private String title;

    public Category(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Category(String title) {
        this.title = title;
    }

    /**
     * Метод переименования категории
     */
    public void rename(String newTitle) {
        this.title = newTitle;
    }

    public String getTitle() {
        return this.title;
    }

    public Object[] getObjects() {
        return new Object[] {
                id,
                title
        };
    }
}
