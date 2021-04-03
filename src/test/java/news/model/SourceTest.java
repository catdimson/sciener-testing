package news.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестирование источника для новостей и мероприятий (Source)
 */
class SourceTest {

    /**
     * Сменить url источника
     */
    @Test
    void editUrl() {
        Source source = new Source("https://lenta.ru/comments/news/2021/04/03/bil/");
        source.editSource("https://lenta.ru/");

        String actualTitle = source.getSourceUrl();
        String expectedTitle = "https://lenta.ru/";

        assertEquals(expectedTitle, actualTitle);
    }
}