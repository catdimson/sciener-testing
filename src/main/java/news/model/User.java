package news.model;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

/**
 * Пользователь
 */
public class User {
    private int id;
    private String password;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate lastLogin;
    final private LocalDate dateJoined;

    private boolean isSuperuser;
    private boolean isStaff;
    private boolean isActive;

    private int groupId;

    public User(int id, String password, String username, String firstName, String lastName, String email, LocalDate lastLogin,
                LocalDate dateJoined, boolean isSuperuser, boolean isStaff, boolean isActive, int groupId) {
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

    public User(String password, String username, String firstName, String lastName, String email, LocalDate lastLogin,
                LocalDate dateJoined, boolean isSuperuser, boolean isStaff, boolean isActive, int groupId) {
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

    /**
     * Изменить имя и фамилию
     */
    public void rewriteFullName(String newFirstName, String newLastName) {
        this.firstName = newFirstName.trim();
        this.lastName = newLastName.trim();
    }

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
    public boolean isPermissionOfSuperuser() {
        return this.isActive && this.isSuperuser;
    }

    /**
     * Проверка, обалает ли пользователь правами персонала и активен ли он
     */
    public boolean isPermissionOfStaff() {
        return this.isStaff && this.isActive;
    }

    public boolean equalsWithId(int id) {
        return this.id == id;
    }

    public boolean equalsWithFirstname(String firstName) {
        return this.firstName.equals(firstName);
    }

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
}
