package news.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Objects;

/**
 * Источник для новостей и афиш
 */
@Entity
@Table(name = "source", schema = "public", catalog = "news_db")
public class Source {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    private int id;

    @Basic
    @Column(name = "title")
    @JsonProperty
    private String title;

    @Basic
    @Column(name = "url")
    @JsonProperty
    private String url;

    public Source() {};

    public Source(int id, String title, String url) {
        this.id = id;
        this.url = url;
        this.title = title;
    }

    public Source(String title, String url) {
        this.title = title;
        this.url = url;
    }

    @JsonIgnore
    public int getSourceId() {
        return this.id;
    }

    public void setSourceId(int id) {
        this.id = id;
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

    @JsonIgnore
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Source that = (Source) o;
        return id == that.id && Objects.equals(title, that.title) && Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, url);
    }
}
