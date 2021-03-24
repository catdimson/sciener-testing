import news.Group;
import news.User;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

class UserTest {
    private static LocalDate lastLogin;
    private static LocalDate dateJoined;
    private static Group group;

    /**
     * Инициализация данных общих для всех тестов
     */
    @BeforeAll
    static void beforeAll() {
        lastLogin = LocalDate.of(2019, 5, 20);
        dateJoined = LocalDate.of(2020, 5, 20);
        group = new Group(1, "editor", null, null);
    }

    /**
     * Получение фамилии и имени пользователя
     */
    @Test
    void getFullName() throws NoSuchAlgorithmException {
        User user = new User(1, "qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", group, lastLogin, dateJoined, true, true, true,
                null, null, null);
        user.changeFirstName("Олег");
        user.changeLastName("Бочаров");

        String expected = "Олег Бочаров";

        Assertions.assertEquals(user.getFullName(), expected);
    }

    /**
     * Изменение данных аккаунта: логин, пароль и email
     */
    @Test
    void changePersonalData() throws NoSuchAlgorithmException {
        User user = new User(1, "qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", group, lastLogin, dateJoined, true, true, true,
                null, null, null);
        SoftAssertions soft = new SoftAssertions();

        user.changeUsername("Иванов");
        user.encodeAndChangePassword("newpas12");
        user.changeEmail("newemail@mail.ru");
        String expectedPassword = user.md5("newpas12");

        soft.assertThat(user)
                .hasFieldOrPropertyWithValue("username", "Иванов")
                .hasFieldOrPropertyWithValue("password", expectedPassword)
                .hasFieldOrPropertyWithValue("email", "newemail@mail.ru");
        soft.assertAll();
    }

    /**
     * Деактивация пользователя, убрать права суперпользователя, сделать его НЕ персоналом
     */
    @Test
    void allDeactivateUser() throws NoSuchAlgorithmException {
        User user = new User(1, "qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", group, lastLogin, dateJoined, true, true, true,
                null, null, null);
        SoftAssertions soft = new SoftAssertions();

        user.offSuperuser();
        user.offStaff();
        user.deactivate();

        soft.assertThat(user)
                .hasFieldOrPropertyWithValue("isSuperuser", false)
                .hasFieldOrPropertyWithValue("isStaff", false)
                .hasFieldOrPropertyWithValue("isActive", false);
        soft.assertAll();
    }

    /**
     * Активировация пользователя, добавить права суперпользователя, сделать его персоналом
     */
    @Test
    void allActivateUser() throws NoSuchAlgorithmException {
        User user = new User(1, "qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", group, lastLogin, dateJoined, false, false, false,
                null, null, null);
        SoftAssertions soft = new SoftAssertions();

        user.onSuperuser();
        user.onStaff();
        user.activate();

        soft.assertThat(user)
                .hasFieldOrPropertyWithValue("isSuperuser", true)
                .hasFieldOrPropertyWithValue("isStaff", true)
                .hasFieldOrPropertyWithValue("isActive", true);
        soft.assertAll();
    }
}



