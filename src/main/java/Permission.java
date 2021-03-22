public class Permission {
    int id;
    String action;
    boolean permission;
    Content content;

    Permission(int id, String action, boolean permission, Content content) {
        this.id = id;
        this.action = action;
        this.permission = permission;
        this.content = content;
    }

    public boolean checkPermission() {
        return this.permission;
    }

    public void onPermission() {
        this.permission = true;
    }

    public void offPermission() {
        this.permission = false;
    }
}
