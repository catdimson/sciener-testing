package news.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тестирование рассылки (Mailing)
 * */
class MailingTest {
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
     * Изменение email
     * */
    @Test
    void changeEmail() {
        Mailing email = new Mailing("qwerty@mail.ru");

        String actualEmail = email.getEmail();
        String expectedEmail = "qwerty@mail.ru";

        assertEquals(expectedEmail, actualEmail);
    }

    /**
     * Отправка почты админом на указанный адрес электронной почты
     * */
    @Test
    void sendEmail() throws NoSuchAlgorithmException, IllegalAccessException {
        User user = new User("qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", lastLogin, dateJoined, false, false, false, groupId);
        Mailing email = new Mailing("subscriber@mail.ru");
        user.activate();
        user.onSuperuser();

        boolean actualStatusSending = email.sendMessage(user);

        Assertions.assertTrue(actualStatusSending);
    }
}