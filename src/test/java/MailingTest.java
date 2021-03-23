import news.Mailing;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MailingTest {

    @Test
    void changeEmail() {
        Mailing email = new Mailing(1, "qwerty@mail.ru");

        String actualEmail = email.getEmail();
        String expectedEmail = "qwerty@mail.ru";

        assertEquals(expectedEmail, actualEmail);
    }
}