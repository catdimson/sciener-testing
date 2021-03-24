import news.Content;
import news.Permission;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class PermissionTest {
    private static Content content;

    /**
     * Инициализация данных общих для всех тестов
     */
    @BeforeAll
    static void beforeAll() {
        content = new Content(1, "News", null);
    }

    /**
     * Функция отключения права на сущность
     */
    @Test
    void offPermission() {
        Permission permission = new Permission(1, "add new", true, content, null);
        permission.offPermission();

        boolean expectedPermission = false;
        boolean actualPermission = permission.getPermission();

        assertEquals(expectedPermission, actualPermission);
    }

    /**
     * Функция включения права на сущность
     */
    @Test
    void onPermission() {
        Permission permission = new Permission(1, "add new", false, content, null);
        permission.onPermission();

        boolean expectedPermission = true;
        boolean actualPermission = permission.getPermission();

        assertEquals(expectedPermission, actualPermission);
    }

}