package news.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Афиша
 * */
public class Afisha {
    private int id;
    private String imageUrl;
    private String lead;
    private String description;
    private int ageLimit;
    private int timing;
    private String place;
    private String phone;
    private LocalDate date;
    private BigDecimal price;
    private int authorUserId;
    private int articleId;

    public Afisha(int id, String imageUrl, String lead, String description, int ageLimit, int timing, String place,
                    String phone, LocalDate date, BigDecimal price, int authorUserId, int articleId) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.lead = lead;
        this.description = description;
        this.ageLimit = ageLimit;
        this.timing = timing;
        this.place = place;
        this.phone = phone;
        this.date = date;
        this.price = price;
        this.authorUserId = authorUserId;
        this.articleId = articleId;
    }

    /**
     * Редактирования новости
     * */
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
