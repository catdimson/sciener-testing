package news.model;

import javax.persistence.*;

/**
 * Категория новости
 */
@Entity
@Table(name = "category", schema = "public", catalog = "news_db")
public class Category {

    @Id
    @Column(name = "id")
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Basic
    //@Column(name = "title", nullable = false, unique = true, length = 50)
    @Column(name = "title")
    private String title;

    public Category() {};

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

    public boolean equalsWithId(int id) {
        return this.id == id;
    }

    public boolean equalsWithTitle(String title) {
        return this.title.equals(title);
    }

    public Object[] getObjects() {
        return new Object[] {
                id,
                title
        };
    }

    public static String[] getFields() {
        return new String[] {
                "id", "title"
        };
    }
}
