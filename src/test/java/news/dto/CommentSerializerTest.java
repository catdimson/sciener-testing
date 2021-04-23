package news.dto;

import news.model.Comment;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommentSerializerTest {
    private static LocalDate createDateArticle;
    private static LocalDate editDateArticle;

    @BeforeAll
    static void beforeAll() {
        createDateArticle = LocalDate.of(2019, 4, 25);
        editDateArticle = LocalDate.of(2019, 6, 25);
    }

    @Test
    void toJSON() {
        Comment comment = new Comment(1,"Текст 1", createDateArticle, editDateArticle, 1, 1);
        Comment.CommentAttachment commentAttachment1 = new Comment.CommentAttachment(1, "Прикрепление 1", "/static/files/file1.png", 1);
        Comment.CommentAttachment commentAttachment2 = new Comment.CommentAttachment(2, "Прикрепление 2", "/static/files/file2.png", 1);
        comment.addNewAttachment(commentAttachment1);
        comment.addNewAttachment(commentAttachment2);
        final String expectedJSON =
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"text\":\"Текст 1\",\n" +
                "\t\"createDate\":" + Timestamp.valueOf(createDateArticle.atStartOfDay()).getTime() / 1000 + ",\n" +
                "\t\"editDate\":" + Timestamp.valueOf(editDateArticle.atStartOfDay()).getTime() / 1000 + ",\n" +
                "\t\"userId\":1,\n" +
                "\t\"articleId\":1,\n" +
                "\t\"attachments\":[\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":1,\n" +
                "\t\t\t\"title\":\"Прикрепление 1\",\n" +
                "\t\t\t\"path\":\"/static/files/file1.png\",\n" +
                "\t\t\t\"commentId\":1,\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":2,\n" +
                "\t\t\t\"title\":\"Прикрепление 2\",\n" +
                "\t\t\t\"path\":\"/static/files/file2.png\",\n" +
                "\t\t\t\"commentId\":1,\n" +
                "\t\t},\n" +
                "\t]\n" +
                "}";

        CommentSerializer commentSerializer = new CommentSerializer(comment);
        String result = commentSerializer.toJSON();

        assertThat(result).isEqualTo(expectedJSON);
    }

    @Test
    void toObject() {
        SoftAssertions soft = new SoftAssertions();
        final String json =
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"text\":\"Текст 1\",\n" +
                "\t\"createDate\":" + Timestamp.valueOf(createDateArticle.atStartOfDay()).getTime() / 1000 + ",\n" +
                "\t\"editDate\":" + Timestamp.valueOf(editDateArticle.atStartOfDay()).getTime() / 1000 + ",\n" +
                "\t\"userId\":1,\n" +
                "\t\"articleId\":1,\n" +
                "\t\"attachments\":[\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":1,\n" +
                "\t\t\t\"title\":\"Прикрепление 1\",\n" +
                "\t\t\t\"path\":\"/static/files/file1.png\",\n" +
                "\t\t\t\"commentId\":1,\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":2,\n" +
                "\t\t\t\"title\":\"Прикрепление 2\",\n" +
                "\t\t\t\"path\":\"/static/files/file2.png\",\n" +
                "\t\t\t\"commentId\":1,\n" +
                "\t\t},\n" +
                "\t]\n" +
                "}";

        CommentSerializer commentSerializer = new CommentSerializer(json);
        Comment comment = commentSerializer.toObject();

        // сверяем данные
        Object[] commentInstance = comment.getObjects();
        List listAttachmentObjects = (ArrayList) commentInstance[6];
        Comment.CommentAttachment commentAttachment1 = (Comment.CommentAttachment) listAttachmentObjects.get(0);
        Comment.CommentAttachment commentAttachment2 = (Comment.CommentAttachment) listAttachmentObjects.get(1);
        soft.assertThat(comment)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("text", "Текст 1")
                .hasFieldOrPropertyWithValue("createDate", createDateArticle)
                .hasFieldOrPropertyWithValue("editDate", editDateArticle)
                .hasFieldOrPropertyWithValue("userId", 1)
                .hasFieldOrPropertyWithValue("articleId", 1);
        soft.assertAll();
        soft.assertThat(commentAttachment1)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("title", "Прикрепление 1")
                .hasFieldOrPropertyWithValue("path", "/static/files/file1.png")
                .hasFieldOrPropertyWithValue("commentId", 1);
        soft.assertAll();
        soft.assertThat(commentAttachment2)
                .hasFieldOrPropertyWithValue("id", 2)
                .hasFieldOrPropertyWithValue("title", "Прикрепление 2")
                .hasFieldOrPropertyWithValue("path", "/static/files/file2.png")
                .hasFieldOrPropertyWithValue("commentId", 1);
        soft.assertAll();
    }
}