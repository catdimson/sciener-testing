import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;


public class User {
    String password;
    String username;
    String firstName;
    String lastName;
    String email;
    int group;
    Date lastLogin;
    Date dateJoined;
    boolean isSuperuser;
    boolean isStaff;
    boolean isActive;

    User(String password, String username, String firstName, String lastName, String email,
         int group, Date lastLogin, Date dateJoined, boolean isSuperuser, boolean isStaff, boolean isActive)
            throws NoSuchAlgorithmException {
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

}
