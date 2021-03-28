import news.model.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class ArticlesTest {
    private static LocalDate editDate;
    private static LocalDate createDate;

    @Mock
    private Image image;

    @Mock
    private Tag tag;

    @Mock
    private User user;

    @Mock
    private Category category;

    @Mock
    final private List<Tag> tags = new ArrayList<>();

    @Mock
    final private List<Image> images = new ArrayList<>();

    @Mock
    final private List<Comment> comments = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {
        editDate = LocalDate.of(2020, 5, 20);
        createDate = LocalDate.of(2020, 5, 20);
    }

    /**
     * Проверка метода редактирования новости
     */
    @Test
    void editArticle() {
        Articles article = new Articles(1, "title 1", "lead 1", createDate, editDate, "description article 1",
                true, category, user, tags, images, comments);
        SoftAssertions soft = new SoftAssertions();

        article.edit("title 2", "lead 2", editDate,
                "description article 2", false, category, user);

        soft.assertThat(article)
                .hasFieldOrPropertyWithValue("title", "title 2")
                .hasFieldOrPropertyWithValue("lead", "lead 2")
                .hasFieldOrPropertyWithValue("editDate", editDate)
                .hasFieldOrPropertyWithValue("text", "description article 2")
                .hasFieldOrPropertyWithValue("isPublished", false)
                .hasFieldOrPropertyWithValue("category", category)
                .hasFieldOrPropertyWithValue("user", user);
        soft.assertAll();
    }

    @Test
    void addNewTag() {
        Articles article = new Articles(1, "title 1", "lead 1", createDate, editDate, "description article 1",
                true, category, user, tags, images, comments);

        article.addNewTag(tag);

        Assertions.assertTrue(article.containTag(tag));
    }

    @Test
    void addNewImage() {
        Articles article = new Articles(1, "title 1", "lead 1", createDate, editDate, "description article 1",
                true, category, user, tags, images, comments);

        article.addNewImage(image);

        Assertions.assertTrue(article.containImage(image));
    }

    @Test
    void unpublished() {
        Articles article = new Articles(1, "title 1", "lead 1", createDate, editDate, "description article 1",
                true, category, user, tags, images, comments);

        article.unpublished();

        Assertions.assertFalse(article.getStatusPublished());
    }

    @Test
    void published() {
        Articles article = new Articles(1, "title 1", "lead 1", createDate, editDate, "description article 1",
                true, category, user, tags, images, comments);

        article.published();

        Assertions.assertTrue(article.getStatusPublished());
    }
}