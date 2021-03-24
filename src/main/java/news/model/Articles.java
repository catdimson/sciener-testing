package news.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Articles {
    final private int id;
    final private LocalDate createDate;
    private String title;
    private String lead;
    private LocalDate editDate;
    private String text;
    private boolean isPublished;
    private Category category;
    private User user;
    final private Collection<Tag> tags = new ArrayList<>();
    final private Collection<Image> images = new ArrayList<>();
    final private Collection<Comment> comments = new ArrayList<>();

    public Articles(int id, String title, String lead, LocalDate createDate, LocalDate editDate, String text, boolean isPublished,
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
        this.tags.addAll(tags);
        this.images.addAll(images);
        this.comments.addAll(comments);
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

    public void addNewTag(Tag tag) {
        this.tags.add(tag);
    }

    public boolean containTag(Tag tag) {
        return this.tags.contains(tag);
    }

    public void addNewImage(Image image) {
        this.images.add(image);
    }

    public boolean containImage(Image image) {
        return this.images.contains(image);
    }

    public void published() {
        this.isPublished = true;
    }

    public void unpublished() {
        this.isPublished = false;
    }

    public boolean getStatusPublished() {
        return this.isPublished;
    }
}
