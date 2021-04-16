package news.model;

/**
 * Группа пользователей
 */
public class Group {
    private int id;
    private String title;

    public Group(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Group(String title) {
        this.title = title;
    }

    /**
     * Переименование группы
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
