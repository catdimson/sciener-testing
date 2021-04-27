package news.dto;

import news.model.Group;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GroupSerializerTest {

    @Test
    void toJSON() {
        Group group = new Group(1, "Редактор");
        final String expectedJSON =
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"title\":\"Редактор\",\n" +
                "}";

        GroupSerializer groupSerializer = new GroupSerializer(group);
        String result = groupSerializer.toJSON();

        assertThat(result).isEqualTo(expectedJSON);
    }

    @Test
    void toObject() {
        SoftAssertions soft = new SoftAssertions();
        final String json =
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"title\":\"Редактор\",\n" +
                "}";

        GroupSerializer groupSerializer = new GroupSerializer(json);
        Group group = groupSerializer.toObject();

        // сверяем данные
        soft.assertThat(group)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("title", "Редактор");
        soft.assertAll();
    }
}