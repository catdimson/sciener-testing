package domain;

import java.util.List;

public class Group {
    final private int id;
    private String title;
    private List<User> users;
    private List<Permission> permissions;


    public Group(int id, String title) {
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
