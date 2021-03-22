import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MailingTest {

    @Test
    void changeEmail() {
        Mailing email = new Mailing(1, "qwerty@mail.ru");

        String actual = email.getEmail();
        String expected = "qwerty@mail.ru";

        assertEquals(expected, actual);
    }
}