package news.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Комментарий к новости
 */
public class Comment {
    private int id;
    final private LocalDate createDate;
    private String text;
    private LocalDate editDate;
    final private int userId;
    private int articleId;
    private Collection<CommentAttachment> attachments = new ArrayList<>();

    public Comment(int id, String text, LocalDate createDate, LocalDate editDate, int userId, int articleId) {
        this.id = id;
        this.text = text;
        this.createDate = createDate;
        this.editDate = editDate;
        this.userId = userId;
        this.articleId = articleId;
    }

    public Comment(String text, LocalDate createDate, LocalDate editDate, int userId, int articleId) {
        this.text = text;
        this.createDate = createDate;
        this.editDate = editDate;
        this.userId = userId;
        this.articleId = articleId;
    }

    /**
     * Прикрепление к комментарию
     */
    public static class CommentAttachment {
        int id;
        String title;
        String path;
        int commentId;

        public CommentAttachment(int id, String title, String path, int commentId) {
            this.id = id;
            this.title = title;
            this.path = path;
            this.commentId = commentId;
        }

        public CommentAttachment(String title, String path, int commentId) {
            this.title = title;
            this.path = path;
            this.commentId = commentId;
        }

        public Object[] getObjects() {
            return new Object[] {
                    id,
                    title,
                    path,
                    commentId
            };
        }
    }

    /**
     * Редактирование комментария
     */
    public void editArticle(String text, LocalDate editDate, int articleId) {
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
                articleId,
                userId,
                attachments
        };
    }
}
