package news.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Комментарий к новости
 * */
public class Comment {
    final private int id;
    final private LocalDate createDate;
    private String text;
    private LocalDate editDate;
    final private int userId;
    private int articleId;

    Collection<Attachment> attachments = new ArrayList<Attachment>();

    class Attachment() {
        int id;
        String path;

    }

    public Comment(int id, String text, LocalDate createDate, LocalDate editDate, int userId, int articleId) {
        this.id = id;
        this.text = text;
        this.createDate = createDate;
        this.editDate = editDate;
        this.userId = userId;
        this.articleId = articleId;
    }

    public void edit(String text, LocalDate editDate, int articleId) {
        this.text = text;
        this.editDate = editDate;
        this.articleId = articleId;
    }
}
