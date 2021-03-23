package news;

import java.time.LocalDate;

public class Comment {
    final private int id;
    final private LocalDate createDate;
    final private User user;
    private String text;
    private LocalDate editDate;
    private New article;

    public Comment(int id, String text, LocalDate createDate, LocalDate editDate, New article, User user) {
        this.id = id;
        this.text = text;
        this.createDate = createDate;
        this.editDate = editDate;
        this.article = article;
        this.user = user;
    }

    public void edit(String text, LocalDate editDate, New article) {
        this.text = text;
        this.editDate = editDate;
        this.article = article;
    }
}
