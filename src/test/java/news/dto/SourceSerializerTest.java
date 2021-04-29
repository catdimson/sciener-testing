package news.dto;

import news.model.Source;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SourceSerializerTest {

    @Test
    void toJSON() {
        Source source = new Source(1, "РИА новости", "https://ria.ru/");
        final String expectedJSON =
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"title\":\"РИА новости\",\n" +
                "\t\"url\":\"https://ria.ru/\"\n" +
                "}";

        SourceSerializer sourceSerializer = new SourceSerializer(source);
        String result = sourceSerializer.toJSON();

        assertThat(result).isEqualTo(expectedJSON);
    }

    @Test
    void toObject() {
        SoftAssertions soft = new SoftAssertions();
        final String json =
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"title\":\"РИА новости\",\n" +
                "\t\"url\":\"https://ria.ru\"\n" +
                "}";

        SourceSerializer sourceSerializer = new SourceSerializer(json);
        Source source = sourceSerializer.toObject();

        // сверяем данные
        soft.assertThat(source)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("title", "РИА новости")
                .hasFieldOrPropertyWithValue("url", "https://ria.ru");
        soft.assertAll();
    }
}