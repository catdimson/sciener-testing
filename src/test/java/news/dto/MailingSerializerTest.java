package news.dto;

import news.model.Mailing;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MailingSerializerTest {

    @Test
    void toJSON() {
        Mailing mailing = new Mailing(1, "test@mail.ru");
        final String expectedJSON =
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"email\":\"test@mail.ru\",\n" +
                "}";

        MailingSerializer mailingSerializer = new MailingSerializer(mailing);
        String result = mailingSerializer.toJSON();

        assertThat(result).isEqualTo(expectedJSON);
    }

    @Test
    void toObject() {
        SoftAssertions soft = new SoftAssertions();
        final String json =
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"email\":\"test@mail.ru\",\n" +
                "}";

        MailingSerializer mailingSerializer = new MailingSerializer(json);
        Mailing mailing = mailingSerializer.toObject();

        // сверяем данные
        soft.assertThat(mailing)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("email", "test@mail.ru");
        soft.assertAll();
    }
}