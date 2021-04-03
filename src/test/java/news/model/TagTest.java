package news.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тестирование тега (Tag)
 */
class TagTest {

    /**
     * Переиманование заголовка тега
     */
    @Test
    void changeTitle() {
        Tag tag = new Tag(1, "it");
        tag.rename("ufc");

        String actual = tag.getTitle();
        String expected = "ufc";

        assertEquals(expected, actual);
    }
}