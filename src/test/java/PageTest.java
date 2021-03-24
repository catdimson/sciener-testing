import news.Content;
import news.Page;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PageTest {
    private static Content content;

    /**
     * Инициализация данных общих для всех тестов
     */
    @BeforeAll
    static void beforeAll() {
        content = new Content(1, "News", null);
    }

    /**
     * Проверка метода редактирования страницы
     */
    @Test
    void editPage() {
        Page page = new Page(1, "title 1", "meta charset 1", "meta description 1",
                "meta keywords 1", "title 1", "/static/favicon.ico", false,
                "/news/all", content);
        SoftAssertions soft = new SoftAssertions();

        page.edit("title 2", "meta charset 2", "meta description 2",
                "meta keywords 2", "title 2", "/static/path/favicon.ico", true,
                "/news/hot", content);

        soft.assertThat(page)
                .hasFieldOrPropertyWithValue("title", "title 2")
                .hasFieldOrPropertyWithValue("metaCharset", "meta charset 2")
                .hasFieldOrPropertyWithValue("metaDescription", "meta description 2")
                .hasFieldOrPropertyWithValue("metaKeywords", "meta keywords 2")
                .hasFieldOrPropertyWithValue("titleMenu", "title 2")
                .hasFieldOrPropertyWithValue("faviconPath", "/static/path/favicon.ico")
                .hasFieldOrPropertyWithValue("isPublished", true)
                .hasFieldOrPropertyWithValue("url", "/news/hot")
                .hasFieldOrPropertyWithValue("content", content);
        soft.assertAll();
    }
}