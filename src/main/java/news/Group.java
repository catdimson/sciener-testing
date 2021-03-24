package news;

import java.util.List;

public class Group {
    final private int id;
    private String title;
    private List<User> users;
    private List<Permission> permissions;


    public Group(int id, String title, List<User> users, List<Permission> permissions) {
        this.id = id;
        this.title = title;
        this.users = users;
        this.permissions = permissions;
    }

    public void changeTitle(String newTitle) {
        this.title = newTitle;
    }

    public String getTitle() {
        return this.title;
    }
}
