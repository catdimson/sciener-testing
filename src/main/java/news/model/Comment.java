package news.model;

import java.time.LocalDate;

public class Comment {
    final private int id;
    final private LocalDate createDate;
    final private User user;
    private String text;
    private LocalDate editDate;
    private Articles article;

    public Comment(int id, String text, LocalDate createDate, LocalDate editDate, Articles article, User user) {
        this.id = id;
        this.text = text;
        this.createDate = createDate;
        this.editDate = editDate;
        this.article = article;
        this.user = user;
    }

    public void edit(String text, LocalDate editDate, Articles article) {
        this.text = text;
        this.editDate = editDate;
        this.article = article;
    }
}
