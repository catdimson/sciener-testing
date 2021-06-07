package news.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.sql.Timestamp;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Тестирование рассылки (Mailing)
 */
class MailingTest {
    private static LocalDate lastLogin;
    private static LocalDate dateJoined;
    private static int groupId;

    @Mock
    User userAdmin = new User("qwerty12", "admin", "alexandr", "kanonenko",
                    "admin@gmail.com", Timestamp.valueOf(lastLogin.atStartOfDay()), Timestamp.valueOf(dateJoined.atStartOfDay()), true, true, true, groupId);

    @Mock
    User userPersonal = new User("qwerty12", "admin", "alexandr", "kanonenko",
                    "admin@gmail.com", Timestamp.valueOf(lastLogin.atStartOfDay()), Timestamp.valueOf(dateJoined.atStartOfDay()), false, true, true, groupId);

    @BeforeAll
    static void beforeAll() {
        lastLogin = LocalDate.of(2019, 5, 20);
        dateJoined = LocalDate.of(2020, 5, 20);
        groupId = 1;
    }

    /**
     * Отправка почты админом на указанный адрес электронной почты
     */
    @Test
    void sendEmailAdmin() throws IllegalAccessException {
        Mailing email = new Mailing("subscriber@mail.ru");

        assertThat(email.sendMessage(userAdmin)).as("Сообщение неотправлено").isTrue();
    }

    /**
     * Отправка почты не админом
    */
    @Test
    void sendEmailNotAdmin() {
        Mailing email = new Mailing(1, "subscriber@mail.ru");

        Throwable throwable = assertThrows(IllegalAccessException.class, () -> email.sendMessage(userPersonal));

        assertThat(throwable.getMessage()).contains("Недостаточно прав");
    }
}