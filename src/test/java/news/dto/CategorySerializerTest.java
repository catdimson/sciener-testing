package news.dto;

import news.model.Category;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategorySerializerTest {

    @Test
    void toJSON() {
        Category category = new Category(1, "Политика");
        final String expectedJSON =
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"title\":\"Политика\",\n" +
                "}";

        CategorySerializer categorySerializer = new CategorySerializer(category);
        String result = categorySerializer.toJSON();

        assertThat(result).isEqualTo(expectedJSON);
    }

    @Test
    void toObject() {
        SoftAssertions soft = new SoftAssertions();
        final String json =
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"title\":\"Политика\",\n" +
                "}";

        CategorySerializer categorySerializer = new CategorySerializer(json);
        Category category = categorySerializer.toObject();

        // сверяем данные
        soft.assertThat(category)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("title", "Политика");
        soft.assertAll();
    }
}