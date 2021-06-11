package news.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Objects;

/**
 * Изображение новости
 */
@Entity
@Table(name = "image", schema = "public", catalog = "news_db")
public class ArticleImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonProperty
    private int id;

    @Basic
    @Column(name = "title")
    @JsonProperty
    private String title;

    @Basic
    @Column(name = "path")
    @JsonProperty
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", referencedColumnName = "id")
    Article article;

    public ArticleImage() {}

    public ArticleImage(int id, String title, String path) {
        this.id = id;
        this.title = title;
        this.path = path;
    }

    public ArticleImage(String title, String path) {
        this.title = title;
        this.path = path;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    @JsonIgnore
    public Object[] getObjects() {
        return new Object[] {
                id,
                title,
                path
                //articleId
        };
    }

    public static String[] getFields() {
        return new String[] {
                "id", "title", "path", "articleId"
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArticleImage that = (ArticleImage) o;
        return id == that.id && Objects.equals(title, that.title) && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, path);
    }
}