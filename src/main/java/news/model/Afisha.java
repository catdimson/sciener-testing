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

    private int userId;
    private int sourceId;

    public Afisha(int id, String title, String imageUrl, String lead, String description, int ageLimit, int timing,
                  String place, String phone, LocalDate date, Boolean isCommercial, int userId, int articleId) {
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
        this.userId = userId;
        this.sourceId = articleId;
    }

    /**
     * Редактирования афиши
     */
    public void edit(String title, String lead, String description, int ageLimit, int timing,
                     String place, String phone, LocalDate date, Boolean isCommercial, int userId, int sourceId) {
        this.title = title;
        this.lead = lead;
        this.description = description;
        this.ageLimit = ageLimit;
        this.timing = timing;
        this.place = place;
        this.phone = phone;
        this.date = date;
        this.isCommercial = isCommercial;
        this.userId = userId;
        this.sourceId = sourceId;
    }

    public Object[] getObjects() {
        return new Object[] {
                id,
                title,
                lead,
                description,
                timing,
                place,
                phone,
                date,
                isCommercial,
                userId,
                sourceId
        };
    }
}
