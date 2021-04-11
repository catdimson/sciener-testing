package news.model;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тестирование пользователя (User)
 * */
class UserTest {
    private static LocalDate lastLogin;
    private static LocalDate dateJoined;
    private static int groupId;

    @BeforeAll
    static void beforeAll() {
        lastLogin = LocalDate.of(2019, 5, 20);
        dateJoined = LocalDate.of(2020, 5, 20);
        groupId = 1;
    }

    /**
     * Получение фамилии и имени пользователя
     */
    @Test
    void rewriteFullName() throws NoSuchAlgorithmException {
        User user = new User("qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", lastLogin, dateJoined, true, true, true, groupId);

        user.rewriteFullName("Олег", "Бочаров");
        String expected = "Олег Бочаров";

        Assertions.assertEquals(user.getFullName(), expected);
    }

    /**
     * Изменение данных аккаунта: логин, пароль и email
     */
    @Test
    void changePersonalData() {
        User user = new User(1, "qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", lastLogin, dateJoined, true, true, true, groupId);
        SoftAssertions soft = new SoftAssertions();

        user.editAccountData("Иванов", "newpas12", "newemail@mail.ru");

        soft.assertThat(user)
                .hasFieldOrPropertyWithValue("username", "Иванов")
                .hasFieldOrPropertyWithValue("password", "newpas12")
                .hasFieldOrPropertyWithValue("email", "newemail@mail.ru");
        soft.assertAll();
    }

    /**
     * Деактивация пользователя, убрать права суперпользователя, сделать его НЕ персоналом
     */
    @Test
    void allDeactivateUser() {
        User user = new User(1, "qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", lastLogin, dateJoined, true, true, true, groupId);
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
    void allActivateUser() {
        User user = new User(1, "qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", lastLogin, dateJoined, false, false, false, groupId);
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

    /**
     * Активен ли пользователь и является ли он администратором
     */
    @Test
    void isPermissionOfSuperuser() {
        User user = new User(1, "qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", lastLogin, dateJoined, true, false, true, groupId);

        user.onSuperuser();
        user.activate();

        assertThat(user.isPermissionOfSuperuser()).as("У объекта пользователя нет прав администратора " +
                "или он неактивен").isTrue();
    }

    /**
     * Активен ли пользователь и является ли он персоналом
     */
    @Test
    void isPermissionOfStaff() {
        User user = new User(1, "qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", lastLogin, dateJoined, true, false, true, groupId);

        user.onStaff();
        user.activate();

        assertThat(user.isPermissionOfStaff()).as("У объекта пользователя нет прав персонала " +
                "или он неактивен").isTrue();
    }
}



