import org.assertj.core.api.SoftAssertions;
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
        content = new Content(1, "News");
    }

    /**
     * Проверка работы конструктора Permission
     */
    @Test
    void Permission() {
        Permission permission = new Permission(1, "add new", true, content);
        SoftAssertions soft = new SoftAssertions();

        soft.assertThat(permission)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("action", "add new")
                .hasFieldOrPropertyWithValue("permission", true)
                .hasFieldOrPropertyWithValue("content", content);
        soft.assertAll();
    }

    /**
     * Функция получения прав на сущьность
     */
    @Test
    void checkPermission() {
        Permission permission = new Permission(1, "add new", true, content);

        boolean expected = true;

        assertEquals(expected, permission.checkPermission());
    }

    /**
     * Функция отключения права на сущность
     */
    @Test
    void offPermission() {
        Permission permission = new Permission(1, "add new", true, content);
        permission.offPermission();

        boolean expected = false;

        assertEquals(expected, permission.checkPermission());
    }

    /**
     * Функция включения права на сущность
     */
    @Test
    void onPermission() {
        Permission permission = new Permission(1, "add new", false, content);
        permission.onPermission();

        boolean expected = true;

        assertEquals(expected, permission.checkPermission());
    }

}