import java.util.Date;

public class Comment {
    int id;
    String text;
    Date createDate;
    Date editDate;
    New article;
    User user;

    Comment(int id, String text, Date createDate, Date editDate, New article, User user) {
        this.id = id;
        this.text = text;
        this.createDate = createDate;
        this.editDate = editDate;
        this.article = article;
        this.user = user;
    }

    public void edit(String text, Date editDate, New article) {
        this.text = text;
        this.editDate = editDate;
        this.article = article;
    }
}
