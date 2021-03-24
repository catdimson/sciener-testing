package news;

import java.time.LocalDate;
import java.util.List;

public class New {
    final private int id;
    final private LocalDate createDate;
    private String title;
    private String lead;
    private LocalDate editDate;
    private String text;
    private boolean isPublished;
    private Category category;
    private User user;
    private List<Tag> tags;
    private List<Image> images;
    private List<Comment> comments;

    public New(int id, String title, String lead, LocalDate createDate, LocalDate editDate, String text, boolean isPublished,
               Category category, User user, List<Tag> tags, List<Image> images, List<Comment> comments) {
        this.id = id;
        this.title = title;
        this.lead = lead;
        this.createDate = createDate;
        this.editDate = editDate;
        this.text = text;
        this.isPublished = isPublished;
        this.category = category;
        this.user = user;
        this.tags = tags;
        this.images = images;
        this.comments = comments;
    }

    public void edit(String title, String lead, LocalDate editDate, String text, boolean isPublished,
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
