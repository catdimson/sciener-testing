package news;

import java.util.List;

public class Permission {
    final private int id;
    final private String action;
    final private Content content;
    private boolean permission;
    private List<Group> groups;

    public Permission(int id, String action, boolean permission, Content content) {
        this.id = id;
        this.action = action;
        this.permission = permission;
        this.content = content;
    }

    public boolean getPermission() {
        return this.permission;
    }

    public void onPermission() {
        this.permission = true;
    }

    public void offPermission() {
        this.permission = false;
    }
}
