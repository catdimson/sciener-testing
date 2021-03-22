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
         int group, Date lastLogin, Date dateJoined, boolean isSuperuser, boolean isStaff, boolean isActive) {
        this.password = password;
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
        return this.firstName + " " + this.lastName.trim();
    }

}
