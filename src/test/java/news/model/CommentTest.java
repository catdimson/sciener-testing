package news.model;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;

/**
 * Тестирование комментария (Comment)
 */
class CommentTest {
    private static LocalDate createDate;
    private static LocalDate editDate;

    @Mock
    final private Comment.CommentAttachment commentAttachment = new Comment.CommentAttachment(1, "Прекрасно");

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
        Comment comment = new Comment(1, "comment 1", createDate, editDate, 1, 1);
        LocalDate editDate2 = LocalDate.of(2020, 6, 20);
        SoftAssertions soft = new SoftAssertions();

        comment.editArticle("comment 2", editDate2, 2);

        soft.assertThat(comment)
                .hasFieldOrPropertyWithValue("text", "comment 2")
                .hasFieldOrPropertyWithValue("articleId", 2)
                .hasFieldOrPropertyWithValue("editDate", editDate2);
        soft.assertAll();
    }

    /**
     * Проверка добавления прикрепления в список прикреплений комментария
     */
    @Test
    void addAttachmentInComment() {
        Comment comment = new Comment(1, "comment 1", createDate, editDate, 1, 1);

        comment.addNewAttachment(commentAttachment);

        Assertions.assertTrue(comment.containAttachment(commentAttachment));
    }
}