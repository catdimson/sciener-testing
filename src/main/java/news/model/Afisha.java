package news.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Афиша
 */
@Entity
@Table(name = "afisha", schema = "public", catalog = "news_db")
public class Afisha {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Basic
    @Column(name = "title")
    private String title;

    @Basic
    @Column(name = "image_url")
    private String imageUrl;

    @Basic
    @Column(name = "lead")
    private String lead;

    @Basic
    @Column(name = "description")
    private String description;

    @Basic
    @Column(name = "age_limit")
    private String ageLimit;

    @Basic
    @Column(name = "timing")
    private String timing;

    @Basic
    @Column(name = "place")
    private String place;

    @Basic
    @Column(name = "phone")
    private String phone;

    @Basic
    @Column(name = "date")
    private Timestamp date;

    @Basic
    @Column(name = "is_commercial")
    private boolean isCommercial;

    @Basic
    @Column(name = "user_id")
    private int userId;

    @Basic
    @Column(name = "source_id")
    private int sourceId;

    public Afisha(int id, String title, String imageUrl, String lead, String description, String ageLimit, String timing,
                  String place, String phone, Timestamp date, Boolean isCommercial, int userId, int sourceId) {
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
        this.sourceId = sourceId;
    }

    public Afisha(String title, String imageUrl, String lead, String description, String ageLimit, String timing,
                  String place, String phone, Timestamp date, Boolean isCommercial, int userId, int sourceId) {
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
        this.sourceId = sourceId;
    }

    public Afisha() {}

    public int getAfishaId() {
        return this.id;
    }

    public void setAfishaId(int id) {
        this.id = id;
    }

    /**
     * Редактирования афиши
     */
    public void edit(String title, String lead, String description, String ageLimit, String timing,
                     String place, String phone, Timestamp date, Boolean isCommercial, int userId, int sourceId) {
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

    public boolean equalsWithId(int id) {
        return this.id == id;
    }

    public boolean equalsWithTitle(String title) {
        return this.title.equals(title);
    }

    public Object[] getObjects() {
        return new Object[] {
                id,
                title,
                imageUrl,
                lead,
                description,
                ageLimit,
                timing,
                place,
                phone,
                date,
                isCommercial,
                userId,
                sourceId
        };
    }

    public static String[] getFields() {
        return new String[] {
                "id", "title", "imageUrl", "lead", "description", "ageLimit", "timing", "place", "phone",
                "date", "isCommercial", "userId", "sourceId"
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Afisha that = (Afisha) o;
        return id == that.id && isCommercial == that.isCommercial && Objects.equals(title, that.title) && Objects.equals(imageUrl, that.imageUrl) && Objects.equals(lead, that.lead) && Objects.equals(description, that.description) && Objects.equals(ageLimit, that.ageLimit) && Objects.equals(timing, that.timing) && Objects.equals(place, that.place) && Objects.equals(phone, that.phone) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, imageUrl, lead, description, ageLimit, timing, place, phone, date, isCommercial);
    }
}
