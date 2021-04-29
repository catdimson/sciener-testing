package news.dto;

import news.model.Tag;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TagSerializerTest {

    @Test
    void toJSON() {
        Tag tag = new Tag(1, "ufc");
        final String expectedJSON =
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"title\":\"ufc\"\n" +
                "}";

        TagSerializer tagSerializer = new TagSerializer(tag);
        String result = tagSerializer.toJSON();

        assertThat(result).isEqualTo(expectedJSON);
    }

    @Test
    void toObject() {
        SoftAssertions soft = new SoftAssertions();
        final String json =
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"title\":\"ufc\"\n" +
                "}";

        TagSerializer tagSerializer = new TagSerializer(json);
        Tag tag = tagSerializer.toObject();

        // сверяем данные
        soft.assertThat(tag)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("title", "ufc");
        soft.assertAll();
    }
}