package news.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Objects;

/**
 * Группа пользователей
 */

@Entity
@Table(name = "group", schema = "public", catalog = "news_db")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonProperty
    private int id;

    @Basic
    @Column(name = "title")
    @JsonProperty
    private String title;

    public Group() {};

    public Group(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Group(String title) {
        this.title = title;
    }

    @JsonIgnore
    public int getGroupId() {
        return this.id;
    }

    public void setGroupId(int id) {
        this.id = id;
    }

    /**
     * Переименование группы
     */
    public void rename(String newTitle) {
        this.title = newTitle;
    }

    @JsonIgnore
    public String getTitle() {
        return this.title;
    }

    public boolean equalsWithId(int id) {
        return this.id == id;
    }

    public boolean equalsWithTitle(String title) {
        return this.title.equals(title);
    }

    @JsonIgnore
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
