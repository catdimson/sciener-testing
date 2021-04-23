package news.dto;

import news.model.Afisha;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AfishaSerializerTest {
    private static LocalDate date;

    @BeforeAll
    static void beforeAll() {
        date = LocalDate.of(2019, 4, 25);
    }

    @Test
    void toJSON() {
        Afisha afisha = new Afisha(1, "Заголовок 1", "/static/afisha/image.png", "Лид 1",
                "Описание 1", "12+", "240", "Соборная площадь", "89205950000", date,
                false, 1, 1);
        final String expectedJSON =
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"title\":\"Заголовок 1\",\n" +
                "\t\"imageUrl\":\"/static/afisha/image.png\",\n" +
                "\t\"lead\":\"Лид 1\",\n" +
                "\t\"description\":\"Описание 1\",\n" +
                "\t\"ageLimit\":\"12+\",\n" +
                "\t\"timing\":\"240\",\n" +
                "\t\"place\":\"Соборная площадь\",\n" +
                "\t\"phone\":\"89205950000\",\n" +
                "\t\"date\":" + Timestamp.valueOf(date.atStartOfDay()).getTime() / 1000 + ",\n" +
                "\t\"isCommercial\":false,\n" +
                "\t\"userId\":1,\n" +
                "\t\"sourceId\":1,\n" +
                "}";

        AfishaSerializer afishaSerializer = new AfishaSerializer(afisha);
        String result = afishaSerializer.toJSON();

        assertThat(result).isEqualTo(expectedJSON);
    }

    @Test
    void toObject() {
        SoftAssertions soft = new SoftAssertions();
        final String json =
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"title\":\"Заголовок 1\",\n" +
                "\t\"imageUrl\":\"/static/afisha/image.png\",\n" +
                "\t\"lead\":\"Лид 1\",\n" +
                "\t\"description\":\"Описание 1\",\n" +
                "\t\"ageLimit\":\"12+\",\n" +
                "\t\"timing\":\"240\",\n" +
                "\t\"place\":\"Соборная площадь\",\n" +
                "\t\"phone\":\"89205950000\",\n" +
                "\t\"date\":" + Timestamp.valueOf(date.atStartOfDay()).getTime() / 1000 + ",\n" +
                "\t\"isCommercial\":false,\n" +
                "\t\"userId\":1,\n" +
                "\t\"sourceId\":1,\n" +
                "}";

        AfishaSerializer afishaSerializer = new AfishaSerializer(json);
        Afisha afisha = afishaSerializer.toObject();

        // сверяем данные
        soft.assertThat(afisha)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("title", "Заголовок 1")
                .hasFieldOrPropertyWithValue("imageUrl", "/static/afisha/image.png")
                .hasFieldOrPropertyWithValue("lead", "Лид 1")
                .hasFieldOrPropertyWithValue("description", "Описание 1")
                .hasFieldOrPropertyWithValue("ageLimit", "12+")
                .hasFieldOrPropertyWithValue("timing", "240")
                .hasFieldOrPropertyWithValue("place", "Соборная площадь")
                .hasFieldOrPropertyWithValue("phone", "89205950000")
                .hasFieldOrPropertyWithValue("date", date)
                .hasFieldOrPropertyWithValue("isCommercial", false)
                .hasFieldOrPropertyWithValue("userId", 1)
                .hasFieldOrPropertyWithValue("sourceId", 1);
        soft.assertAll();
    }
}