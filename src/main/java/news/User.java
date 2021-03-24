package news;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;

public class User {
    final private int id;
    final private LocalDate dateJoined;
    private String password;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Group group;
    private LocalDate lastLogin;
    private boolean isSuperuser;
    private boolean isStaff;
    private boolean isActive;
    private List<Comment> comments;
    private List<New> news;
    private List<Log> logs;

    public User(int id, String password, String username, String firstName, String lastName, String email,
                Group group, LocalDate lastLogin, LocalDate dateJoined, boolean isSuperuser, boolean isStaff,
                boolean isActive, List<Comment> comments, List<New> news, List<Log> logs)
            throws NoSuchAlgorithmException {
        this.id = id;
        this.password = md5(password);
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.group = group;
        this.lastLogin = lastLogin;
        this.dateJoined = dateJoined;
        this.isSuperuser = isSuperuser;
        this.isStaff = isStaff;
        this.isActive = isActive;
        this.comments = comments;
        this.news = news;
        this.logs = logs;
    }

    public void changeFirstName(String newFirstName) {
        this.firstName = newFirstName.trim();
    }

    public void changeLastName(String newLastName) {
        this.lastName = newLastName.trim();
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public String md5(String password) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(StandardCharsets.UTF_8.encode(password));
        return String.format("%032x", new BigInteger(1, md5.digest()));
    }

    public void changeUsername(String newUsername) {
        this.username = newUsername;
    }

    public void encodeAndChangePassword(String newPassword) throws NoSuchAlgorithmException {
        this.password = md5(newPassword);
    }

    public void changeEmail(String newEmail) {
        this.email = newEmail;
    }

    public boolean deactivate() {
        this.isActive = false;
        return false;
    }

    public boolean activate() {
        this.isActive = true;
        return true;
    }

    public boolean offSuperuser() {
        this.isSuperuser = false;
        return false;
    }

    public boolean onSuperuser() {
        this.isSuperuser = true;
        return true;
    }

    public boolean offStaff() {
        this.isStaff = false;
        return false;
    }

    public boolean onStaff() {
        this.isStaff = true;
        return true;
    }
}
