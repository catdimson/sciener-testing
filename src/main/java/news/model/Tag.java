package news.model;

import javax.persistence.*;

/**
 * Тэг новости
 */
@Entity
@Table(name = "tag", schema = "public", catalog = "news_db")
public class Tag {

    @Id
    @Column(name = "id")
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Basic
    //@Column(name = "title", nullable = false, length = 50)
    @Column(name = "title")
    private String title;

    public Tag() {};

    public Tag(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Tag(String title) {
        this.title = title;
    }

    /**
     * Переименование новости
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
