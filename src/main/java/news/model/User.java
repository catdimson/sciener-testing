package news.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Пользователь
 */
@Entity
@Table(name = "user", schema = "public", catalog = "news_db")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonProperty
    private int id;

    @Basic
    @Column(name = "password")
    @JsonProperty
    private String password;

    @Basic
    @Column(name = "username")
    @JsonProperty
    private String username;

    @Basic
    @Column(name = "first_name")
    @JsonProperty
    private String firstName;

    @Basic
    @Column(name = "last_name")
    @JsonProperty
    private String lastName;

    @Basic
    @Column(name = "email")
    @JsonProperty
    private String email;

    @Basic
    @Column(name = "last_login")
    @JsonProperty
    private Timestamp lastLogin;

    @Basic
    @Column(name = "date_joined")
    @JsonProperty
    private Timestamp dateJoined;

    @Basic
    @Column(name = "is_superuser")
    @JsonProperty
    private boolean isSuperuser;

    @Basic
    @Column(name = "is_staff")
    @JsonProperty
    private boolean isStaff;

    @Basic
    @Column(name = "is_active")
    @JsonProperty
    private boolean isActive;

    @Basic
    @Column(name = "group_id")
    @JsonProperty
    private int groupId;

    public User() {};

    public User(int id, String password, String username, String firstName, String lastName, String email, Timestamp lastLogin,
                Timestamp dateJoined, boolean isSuperuser, boolean isStaff, boolean isActive, int groupId) {
        this.id = id;
        this.password = password;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.lastLogin = lastLogin;
        this.dateJoined = dateJoined;
        this.isSuperuser = isSuperuser;
        this.isStaff = isStaff;
        this.isActive = isActive;
        this.groupId = groupId;
    }

    public User(String password, String username, String firstName, String lastName, String email, Timestamp lastLogin,
                Timestamp dateJoined, boolean isSuperuser, boolean isStaff, boolean isActive, int groupId) {
        this.password = password;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.lastLogin = lastLogin;
        this.dateJoined = dateJoined;
        this.isSuperuser = isSuperuser;
        this.isStaff = isStaff;
        this.isActive = isActive;
        this.groupId = groupId;
    }

    @JsonIgnore
    public int getUserId() {
        return this.id;
    }

    public void setUserId(int id) {
        this.id = id;
    }

    /**
     * Изменить имя и фамилию
     */
    public void rewriteFullName(String newFirstName, String newLastName) {
        this.firstName = newFirstName.trim();
        this.lastName = newLastName.trim();
    }

    @JsonIgnore
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    /**
     * Хэшировать пароль
     */
    public String md5(String password) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(StandardCharsets.UTF_8.encode(password));
        return String.format("%032x", new BigInteger(1, md5.digest()));
    }

    /**
     * Изменить логин, пароль, email
     */
    public void editAccountData(String newUsername, String newPassword, String newEmail) {
        this.username = newUsername;
        this.password = newPassword;
        this.email = newEmail;
    }

    /**
     * Деактивация/активация пользователя
     */
    public boolean deactivate() {
        this.isActive = false;
        return false;
    }

    public boolean activate() {
        this.isActive = true;
        return true;
    }

    /**
     * Снятие/назначение прав администратора
     */
    public boolean offSuperuser() {
        this.isSuperuser = false;
        return false;
    }

    public boolean onSuperuser() {
        this.isSuperuser = true;
        return true;
    }

    /**
     * Снятие/назначение прав персонала
     */
    public boolean offStaff() {
        this.isStaff = false;
        return false;
    }

    public boolean onStaff() {
        this.isStaff = true;
        return true;
    }

    /**
     * Проверка, обалает ли пользователь правами админа и активен ли он
     */
    @JsonIgnore
    public boolean isPermissionOfSuperuser() {
        return this.isActive && this.isSuperuser;
    }

    /**
     * Проверка, обалает ли пользователь правами персонала и активен ли он
     */
    @JsonIgnore
    public boolean isPermissionOfStaff() {
        return this.isStaff && this.isActive;
    }

    public boolean equalsWithId(int id) {
        return this.id == id;
    }

    public boolean equalsWithFirstname(String firstName) {
        return this.firstName.equals(firstName);
    }

    @JsonIgnore
    public Object[] getObjects() {
        return new Object[] {
                id,
                password,
                username,
                firstName,
                lastName,
                email,
                lastLogin,
                dateJoined,
                isSuperuser,
                isStaff,
                isActive,
                groupId
        };
    }

    public static String[] getFields() {
        return new String[] {
                "id", "password", "username", "firstName", "lastName", "email", "lastLogin", "dateJoined", "isSuperuser",
                "isStaff", "isActive", "groupId"
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return id == that.id && isSuperuser == that.isSuperuser && isStaff == that.isStaff && isActive == that.isActive && Objects.equals(password, that.password) && Objects.equals(username, that.username) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(email, that.email) && Objects.equals(lastLogin, that.lastLogin) && Objects.equals(dateJoined, that.dateJoined);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, password, username, firstName, lastName, email, lastLogin, dateJoined, isSuperuser, isStaff, isActive);
    }
}
