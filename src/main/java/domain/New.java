package domain;

import java.util.Date;
import java.util.List;

public class New {
    final private int id;
    private String title;
    private String lead;
    final private Date createDate;
    private Date editDate;
    private String text;
    private boolean isPublished;
    private Category category;
    private User user;
    private List<Tag> tags;
    private List<Image> images;
    private List<Comment> comments;

    public New(int id, String title, String lead, Date createDate, Date editDate, String text, boolean isPublished,
        Category category, User user) {
        this.id = id;
        this.title = title;
        this.lead = lead;
        this.createDate = createDate;
        this.editDate = editDate;
        this.text = text;
        this.isPublished = isPublished;
        this.category = category;
        this.user = user;
    }

    public void edit(String title, String lead, Date editDate, String text, boolean isPublished,
                     Category category, User user) {
        this.title = title;
        this.lead = lead;
        this.editDate = editDate;
        this.text = text;
        this.isPublished = isPublished;
        this.category = category;
        this.user = user;
    }
}
