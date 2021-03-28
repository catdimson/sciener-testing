import news.model.Content;
import news.model.Permission;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;


class PermissionTest {

    @Mock
    private Content content;

    @Test
    void offPermission() {
        Permission permission = new Permission(1, "add new", true, content);
        permission.offPermission();

        boolean actualPermission = permission.getPermission();

        Assertions.assertFalse(actualPermission);
    }

    @Test
    void onPermission() {
        Permission permission = new Permission(1, "add new", false, content);
        permission.onPermission();

        boolean actualPermission = permission.getPermission();

        Assertions.assertTrue(actualPermission);
    }

}