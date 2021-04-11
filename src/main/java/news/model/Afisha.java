package news.model;

import java.time.LocalDate;

/**
 * Афиша
 */
public class Afisha {
    private int id;
    private String title;
    private String imageUrl;
    private String lead;
    private String description;
    private int ageLimit;
    private int timing;
    private String place;
    private String phone;
    private LocalDate date;
    private boolean isCommercial;

    private int authorUserId;
    private int sourceId;

    public Afisha(int id, String title, String imageUrl, String lead, String description, int ageLimit, int timing,
                  String place, String phone, LocalDate date, Boolean isCommercial, int authorUserId, int articleId) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.lead = lead;
        this.description = description;
        this.ageLimit = ageLimit;
        this.timing = timing;
        this.place = place;
        this.phone = phone;
        this.date = date;
        this.isCommercial = isCommercial;
        this.authorUserId = authorUserId;
        this.sourceId = articleId;
    }

    /**
     * Редактирования афиши
     */
    public void edit(String title, String lead, String description, int ageLimit, int timing,
                     String place, String phone, LocalDate date, Boolean isCommercial, int authorUserId, int sourceId) {
        this.title = title;
        this.lead = lead;
        this.description = description;
        this.ageLimit = ageLimit;
        this.timing = timing;
        this.place = place;
        this.phone = phone;
        this.date = date;
        this.isCommercial = isCommercial;
        this.authorUserId = authorUserId;
        this.sourceId = sourceId;
    }
}
