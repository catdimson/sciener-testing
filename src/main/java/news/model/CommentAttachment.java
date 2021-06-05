package news.model;

import javax.persistence.*;
import java.util.Objects;

/**
 * Прикрепление к комментарию
 */
@Entity
@Table(name = "attachment", schema = "public", catalog = "news_db")
public class CommentAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;

    @Basic
    @Column(name = "title")
    String title;

    @Basic
    @Column(name = "path")
    String path;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "comment_id", referencedColumnName = "id")
    Comment comment;

    public CommentAttachment() {}

    public CommentAttachment(int id, String title, String path) {
        this.id = id;
        this.title = title;
        this.path = path;
    }

    public CommentAttachment(String title, String path) {
        this.title = title;
        this.path = path;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Object[] getObjects() {
        return new Object[] {
                id,
                title,
                path,
                //commentId
        };
    }

    public static String[] getFields() {
        return new String[] {
                "id", "title", "path", "commentId"
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentAttachment that = (CommentAttachment) o;
        return id == that.id && Objects.equals(title, that.title) && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, path);
    }
}