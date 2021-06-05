package news.model;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.sql.Timestamp;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тестирование комментария (Comment)
 */
class CommentTest {
    private static LocalDate createDate;
    private static LocalDate editDate;

    @Mock
    final private CommentAttachment commentAttachment = new CommentAttachment(1, "Документ", "/static/files/file1.txt", 1);

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
        Comment comment = new Comment(1, "comment 1", Timestamp.valueOf(createDate.atStartOfDay()),
                Timestamp.valueOf(editDate.atStartOfDay()), 1, 1);
        LocalDate editDate2 = LocalDate.of(2020, 6, 20);
        SoftAssertions soft = new SoftAssertions();

        comment.editArticle("comment 2", Timestamp.valueOf(editDate2.atStartOfDay()), 2);

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
        Comment comment = new Comment(1, "comment 1", Timestamp.valueOf(createDate.atStartOfDay()),
                Timestamp.valueOf(editDate.atStartOfDay()), 1, 1);

        comment.addNewAttachment(commentAttachment);

        assertThat(comment.containAttachment(commentAttachment)).as("Прикрепление не было добавлено к " +
                "комментарию").isTrue();
    }
}