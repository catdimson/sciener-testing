package news.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Objects;

/**
 * Категория новости
 */
@Entity
@Table(name = "category", schema = "public", catalog = "news_db")
public class Category {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    private int id;

    @Basic
    @Column(name = "title")
    @JsonProperty
    private String title;

    public Category() {};

    public Category(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Category(String title) {
        this.title = title;
    }

    @JsonIgnore
    public int getCategoryId() {
        return this.id;
    }

    public void setCategoryId(int id) {
        this.id = id;
    }

    /**
     * Метод переименования категории
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
        Category that = (Category) o;
        return id == that.id && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }
}
