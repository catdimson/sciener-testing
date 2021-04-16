package news.model;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

/**
 * Тестирование источника для новостей и мероприятий (Source)
 */
class SourceTest {

    /**
     * Сменить url источника
     */
    @Test
    void editUrl() {
        Source source = new Source(1, "Рамблер новости", "https://rambler.ru/comments/news/2021/04/03/bil/");
        SoftAssertions soft = new SoftAssertions();

        source.editSource("https://lenta.ru/", "Лента РУ");

        soft.assertThat(source)
                .hasFieldOrPropertyWithValue("url", "https://lenta.ru/")
                .hasFieldOrPropertyWithValue("title", "Лента РУ");
        soft.assertAll();
    }
}