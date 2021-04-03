package news.model;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;

class CommentTest {
    private static LocalDate createDate;
    private static LocalDate editDate;

    @Mock
    private User user;

    @Mock
    private Article article;

    /**
     * Инициализация данных общих для всех тестов
     */
    @BeforeAll
    static void beforeAll() {
        editDate = LocalDate.of(2020, 4, 20);
        createDate = LocalDate.of(2020, 4, 20);
    }

    /**
     * Проверка метода редактирования изображения
     */
    @Test
    void editComment() {
        Comment comment = new Comment(1, "comment 1", createDate, editDate, article, user);
        SoftAssertions soft = new SoftAssertions();

        comment.edit("comment 2", editDate, article);

        soft.assertThat(comment)
                .hasFieldOrPropertyWithValue("text", "comment 2")
                .hasFieldOrPropertyWithValue("article", article)
                .hasFieldOrPropertyWithValue("editDate", editDate);
        soft.assertAll();
    }
}