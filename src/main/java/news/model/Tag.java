package news.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Тэг новости
 */
@Entity
@Table(name = "tag", schema = "public", catalog = "news_db")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonProperty
    private int id;

    @Basic
    @Column(name = "title")
    @JsonProperty
    private String title;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.EAGER)
    private Collection<Article> articles = new HashSet<>();

    public void addNewArticle(Article article) {
        this.articles.add(article);
    }

    public Tag() {};

    public Tag(int id, String title) {
        this.id = id;
        this.title = title;
    }

    @JsonIgnore
    public int getTagId() {
        return this.id;
    }

    public void setTagId(int id) {
        this.id = id;
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
        Tag tagEntity = (Tag) o;
        return id == tagEntity.id && Objects.equals(title, tagEntity.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }
}
