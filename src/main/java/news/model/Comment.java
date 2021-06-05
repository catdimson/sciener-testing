package news.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Комментарий к новости
 */
@Entity
@Table(name = "comment", schema = "public", catalog = "news_db")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Basic
    @Column(name = "text")
    private String text;

    @Basic
    @Column(name = "create_date")
    private Timestamp createDate;

    @Basic
    @Column(name = "edit_date")
    private Timestamp editDate;

    @Basic
    @Column(name = "user_id")
    private int userId;

    @Basic
    @Column(name = "article_id")
    private int articleId;

    @OneToMany(mappedBy = "comment", orphanRemoval=true, cascade = CascadeType.ALL)
    private final Collection<CommentAttachment> attachments = new ArrayList<>();

    public Comment() {}

    public Comment(int id, String text, Timestamp createDate, Timestamp editDate, int userId, int articleId) {
        this.id = id;
        this.text = text;
        this.createDate = createDate;
        this.editDate = editDate;
        this.userId = userId;
        this.articleId = articleId;
    }

    public Comment(String text, Timestamp createDate, Timestamp editDate, int userId, int articleId) {
        this.text = text;
        this.createDate = createDate;
        this.editDate = editDate;
        this.userId = userId;
        this.articleId = articleId;
    }

    public int getCommentId() {
        return this.id;
    }

    public void setCommentId(int id) {
        this.id = id;
    }

    public Collection<CommentAttachment> getAttachments() {
        return this.attachments;
    }

    /**
     * Редактирование комментария
     */
    public void editArticle(String text, Timestamp editDate, int articleId) {
        this.text = text;
        this.editDate = editDate;
        this.articleId = articleId;
    }

    /**
     * Добавление прикрепления в список прикреплений комментария
     */
    public void addNewAttachment(CommentAttachment attachment) {
        this.attachments.add(attachment);
    }

    /**
     * Проверка наличия прикрепления в списке прикреплений
     */
    public boolean containAttachment(CommentAttachment attachment) {
        return this.attachments.contains(attachment);
    }

    public boolean equalsWithId(int id) {
        return this.id == id;
    }

    public boolean equalsWithUserId(int userId) {
        return this.userId == userId;
    }

    public Object[] getObjects() {
        return new Object[] {
                id,
                text,
                createDate,
                editDate,
                userId,
                articleId,
                attachments
        };
    }

    public static String[] getFields() {
        return new String[] {
                "id", "text", "createDate", "editDate", "userId", "articleId", "attachments"
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment that = (Comment) o;
        return id == that.id && Objects.equals(text, that.text) && Objects.equals(createDate, that.createDate) && Objects.equals(editDate, that.editDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, createDate, editDate);
    }
}
