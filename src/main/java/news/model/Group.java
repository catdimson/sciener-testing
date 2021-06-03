package news.model;

import javax.persistence.*;
import java.util.Objects;

/**
 * Группа пользователей
 */

@Entity
@Table(name = "group", schema = "public", catalog = "news_db")
public class Group {

    @Id
    @Column(name = "id")
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Basic
    //@Column(name = "title", nullable = false, unique = true, length = 40)
    @Column(name = "title")
    private String title;

    public Group() {};

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group that = (Group) o;
        return id == that.id && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }
}
