package news.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Group {
    final private int id;
    private String title;
    final private Collection<User> users = new ArrayList<>();
    final private Collection<Permission> permissions = new ArrayList<>();

    public Group(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Group(int id, String title, List<User> users, List<Permission> permissions) {
        this.id = id;
        this.title = title;
        this.users.addAll(users);
        this.permissions.addAll(permissions);
    }

    public void addNewUser(User user) {
        this.users.add(user);
    }

    public void addNewPermission(Permission permission) {
        this.permissions.add(permission);
    }

    public boolean containUser(User user) {
        return this.users.contains(user);
    }

    public boolean containPermission(Permission permission) {
        return this.permissions.contains(permission);
    }

    public void rename(String newTitle) {
        this.title = newTitle;
    }

    public String getTitle() {
        return this.title;
    }
}
