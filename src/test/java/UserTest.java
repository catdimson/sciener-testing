import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

class UserTest {
    private static Date lastLogin;
    private static Date dateJoined;
    private static Group group;

    @BeforeAll
    static void beforeAll() {
        lastLogin = new Date();
        dateJoined = new Date(16_000_000_000_00L);
        group = new Group(1, "editor");
    }

    /**
    * Проверяем работу конструктора User
    */
    @Test
    void User() throws NoSuchAlgorithmException {
        User user = new User("qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", group, lastLogin, dateJoined, true, true, true);
        SoftAssertions soft = new SoftAssertions();

        String expectedPassword = user.md5("qwerty12");

        soft.assertThat(user)
            .hasFieldOrPropertyWithValue("password", expectedPassword)
            .hasFieldOrPropertyWithValue("username", "admin")
            .hasFieldOrPropertyWithValue("firstName", "alexandr")
            .hasFieldOrPropertyWithValue("lastName", "kanonenko")
            .hasFieldOrPropertyWithValue("email", "admin@gmail.com")
            .hasFieldOrPropertyWithValue("group", group)
            .hasFieldOrPropertyWithValue("lastLogin", lastLogin)
            .hasFieldOrPropertyWithValue("dateJoined", dateJoined)
            .hasFieldOrPropertyWithValue("isSuperuser", true)
            .hasFieldOrPropertyWithValue("isStaff", true)
            .hasFieldOrPropertyWithValue("isActive", true);
        soft.assertAll();
    }

    /**
     * Получаем фамилию и имя
     */
    @Test
    void getFullName() throws NoSuchAlgorithmException {
        User user = new User("qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", group, lastLogin, dateJoined, true, true, true);
        user.changeFirstName("Олег");
        user.changeLastName("Бочаров");

        String expected = "Олег Бочаров";

        Assertions.assertEquals(user.getFullName(), expected);
    }

    /**
     * Изменяем данные аккаунта: логин, пароль и email
     */
    @Test
    void changePersonalData() throws NoSuchAlgorithmException {
        User user = new User("qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", group, lastLogin, dateJoined, true, true, true);
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
     * Деактивировать пользователя, убрать права суперпользователя, сделать его НЕ персоналом
     */
    @Test
    void allDeactivateUser() throws NoSuchAlgorithmException {
        User user = new User("qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", group, lastLogin, dateJoined, true, true, true);
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
     * Активировать пользователя, добавить права суперпользователя, сделать его персоналом
     */
    @Test
    void allActivateUser() throws NoSuchAlgorithmException {
        User user = new User("qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", group, lastLogin, dateJoined, false, false, false);
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



