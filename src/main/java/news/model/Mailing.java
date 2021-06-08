package news.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Objects;

/**
 * Рассылка
 */
@Entity
@Table(name = "mailing", schema = "public", catalog = "news_db")
public class Mailing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonProperty
    private int id;

    @Basic
    @Column(name = "email")
    @JsonProperty
    private String email;

    public Mailing(int id, String email) {
        this.id = id;
        this.email = email;
    }

    public Mailing() {};

    public Mailing(String email) {
        this.email = email;
    }

    @JsonIgnore
    public int getMailingId() {
        return this.id;
    }

    public void setMailingId(int id) {
        this.id = id;
    }

    @JsonIgnore
    public String getEmail() {
        return this.email;
    }

    /**
     * Отправка письма
     */
    public boolean sendMessage(User user) throws IllegalAccessException {
        if (user.isPermissionOfSuperuser()) {
            /* Код отправки письма */
            return true;
        } else {
            throw new IllegalAccessException("Недостаточно прав");
        }
    }

    public boolean equalsWithId(int id) {
        return this.id == id;
    }

    public boolean equalsWithEmail(String email) {
        return this.email.equals(email);
    }

    @JsonIgnore
    public Object[] getObjects() {
        return new Object[] {
                id,
                email
        };
    }

    public static String[] getFields() {
        return new String[] {
                "id", "email"
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mailing that = (Mailing) o;
        return id == that.id && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
}