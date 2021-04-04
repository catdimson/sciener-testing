package news.model;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;

/**
 * Тестирование новостей (Article)
 */
class ArticlesTest {
    private static LocalDate editDate;
    private static LocalDate createDate;

    @Mock
    final private Article.ArticleImage articleImage = new Article.ArticleImage(1, "Забастовка на площади",
            "/static/images/zabastovka.jpg");

    @BeforeAll
    static void beforeAll() {
        editDate = LocalDate.of(2020, 5, 20);
        createDate = LocalDate.of(2020, 5, 20);
    }

    /**
     * Редактирования новости
     */
    @Test
    void editArticle() {
        Article article = new Article(1, "title 1", "lead 1", createDate, editDate,
                "description article 1", true, 1, 1, 1);
        SoftAssertions soft = new SoftAssertions();

        article.edit("title 2", "lead 2", editDate,
                "description article 2", false, 2, 2, 2);

        soft.assertThat(article)
                .hasFieldOrPropertyWithValue("title", "title 2")
                .hasFieldOrPropertyWithValue("lead", "lead 2")
                .hasFieldOrPropertyWithValue("editDate", editDate)
                .hasFieldOrPropertyWithValue("text", "description article 2")
                .hasFieldOrPropertyWithValue("isPublished", false)
                .hasFieldOrPropertyWithValue("categoryId", 2)
                .hasFieldOrPropertyWithValue("userId", 2)
                .hasFieldOrPropertyWithValue("sourceId", 2);
        soft.assertAll();
    }

    /**
     * Добавить картинку к новости
     * */
    @Test
    void addNewImage() {
        Article article = new Article(1, "title 1", "lead 1", createDate, editDate,
                "description article 1", true, 1, 1, 1);

        article.addNewImage(articleImage);

        Assertions.assertTrue(article.containImage(articleImage));
    }

    /**
     * Снять новость с публикации
     * */
    @Test
    void unpublished() {
        Article article = new Article(1, "title 1", "lead 1", createDate, editDate,
                "description article 1", true, 1, 1, 1);

        article.unpublished();

        Assertions.assertFalse(article.getStatusPublished());
    }

    /**
     * Опубликовать новость
     * */
    @Test
    void published() {
        Article article = new Article(1, "title 1", "lead 1", createDate, editDate,
                "description article 1", true, 1, 1, 1);

        article.published();

        Assertions.assertTrue(article.getStatusPublished());
    }
}