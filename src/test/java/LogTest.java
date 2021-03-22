import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

class LogTest {
    private static User user;
    private static Content content;
    private static Date actionTime;

    /**
     * Инициализация данных общих для всех тестов
     */
    @BeforeAll
    static void beforeAll() throws NoSuchAlgorithmException {
        Date lastLogin = new Date();
        Date dateJoined = new Date(16_000_000_000_00L);
        Group group = new Group(1, "editor");
        user = new User(1, "qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", group, lastLogin, dateJoined, true, true, true);
        content = new Content(1, "News");
        actionTime = new Date();
    }

    /**
     * Проверка работы конструктора Log
     */
    @Test
    void Log() {
        Log log = new Log(1, "add new", actionTime, content, user);
        SoftAssertions soft = new SoftAssertions();

        soft.assertThat(log)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("action", "add new")
                .hasFieldOrPropertyWithValue("actionTime", actionTime)
                .hasFieldOrPropertyWithValue("content", content)
                .hasFieldOrPropertyWithValue("user", user);
        soft.assertAll();
    }
}