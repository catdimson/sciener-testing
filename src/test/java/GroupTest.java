import news.model.Group;
import news.model.Permission;
import news.model.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GroupTest {

    @Mock
    private User user;

    @Mock
    private Permission permission;

    @Mock
    final private List<User> users = new ArrayList<>();

    @Mock
    final private List<Permission> permissions = new ArrayList<>();

    @Test
    void addNewUser() {
        Group group = new Group(1, "admin", users, permissions);

        group.addNewUser(user);

        assertTrue(group.containUser(user));
    }

    @Test
    void addNewPermission() {
        Group group = new Group(1, "admin", users, permissions);

        group.addNewPermission(permission);

        assertTrue(group.containPermission(permission));
    }

    @Test
    void changeTitle() {
        Group group = new Group(1, "admin");
        group.rename("editor");

        String actualTitle = group.getTitle();
        String expectedTitle = "editor";

        assertEquals(expectedTitle, actualTitle);
    }
}
