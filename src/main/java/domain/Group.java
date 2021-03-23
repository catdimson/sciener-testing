import java.util.List;

public class Group {
    int id;
    String title;
    List<User> users;
    List<Permission> permissions;


    Group(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public void changeTitle(String newTitle) {
        this.title = newTitle;
    }

    public String getTitle() {
        return this.title;
    }
}
