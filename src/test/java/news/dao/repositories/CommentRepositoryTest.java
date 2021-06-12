package news.dao.repositories;

import news.model.Comment;
import news.model.CommentAttachment;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тестирование репозитория для Comment")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = CommentRepositoryTest.Initializer.class)
class CommentRepositoryTest {
    private static Timestamp createDateComment;
    private static Timestamp editDateComment;

    @BeforeAll
    static void setUp() {
        createDateComment = new Timestamp(1561410000000L);
        editDateComment = new Timestamp(1561410000000L);
    }

    @Autowired
    private CommentRepository commentRepository;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.2")
            .withPassword("testrootroot")
            .withUsername("testroot")
            .withDatabaseName("testnewdb");

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues values = TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername()
            );
            values.applyTo(configurableApplicationContext);
        }
    }

    @DisplayName("Получение по ID")
    @Test
    @Transactional
    @Sql(scripts = "classpath:repository-scripts/deployment/comment.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/comment.sql")
    void findById() {
        SoftAssertions soft = new SoftAssertions();
        // статьи
        Comment comment1 = new Comment("Текст 2", createDateComment, editDateComment, 2, 1);
        // прикрепления
        CommentAttachment commentAttachment1 = new CommentAttachment("Прикрепление 2", "/static/attachments/attachment2.png");
        comment1.addNewAttachment(commentAttachment1);
        commentAttachment1.setComment(comment1);

        // получаем список статей
        Optional<Comment> resultComment = commentRepository.findById(2);
        Comment resultComment1 = resultComment.get();
        // получаем изображения
        CommentAttachment resultCommentAttachment1 = (CommentAttachment) resultComment1.getAttachments().toArray()[0];

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(resultComment1)
                .hasFieldOrPropertyWithValue("text", comment1.getObjects()[1])
                .hasFieldOrPropertyWithValue("createDate", comment1.getObjects()[2])
                .hasFieldOrPropertyWithValue("editDate", comment1.getObjects()[3])
                .hasFieldOrPropertyWithValue("userId", comment1.getObjects()[4])
                .hasFieldOrPropertyWithValue("articleId", comment1.getObjects()[5]);
        soft.assertAll();
        soft.assertThat(resultCommentAttachment1)
                .hasFieldOrPropertyWithValue("title", commentAttachment1.getObjects()[1])
                .hasFieldOrPropertyWithValue("path", commentAttachment1.getObjects()[2]);
        soft.assertAll();
    }

    @DisplayName("Получение всех записей")
    @Test
    @Transactional
    @Sql(scripts = "classpath:repository-scripts/deployment/comment.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/comment.sql")
    void findAll() {
        SoftAssertions soft = new SoftAssertions();
        // комментарии
        Comment comment1 = new Comment("Текст 1", createDateComment, editDateComment, 1, 1);
        Comment comment2 = new Comment("Текст 2", createDateComment, editDateComment, 2, 1);
        Comment comment3 = new Comment("Текст 3", createDateComment, editDateComment, 1, 1);
        // прикрепления
        CommentAttachment commentAttachment1 = new CommentAttachment("Прикрепление 1", "/static/attachments/attachment1.png");
        CommentAttachment commentAttachment2 = new CommentAttachment("Прикрепление 2", "/static/attachments/attachment2.png");
        comment1.addNewAttachment(commentAttachment1);
        comment2.addNewAttachment(commentAttachment2);
        commentAttachment1.setComment(comment1);
        commentAttachment2.setComment(comment2);

        // получаем список комментариев
        List<Comment> resultComment = commentRepository.findAll();
        Comment resultComment1 = resultComment.get(0);
        Comment resultComment2 = resultComment.get(1);
        Comment resultComment3 = resultComment.get(2);
        // получаем прикрепления
        CommentAttachment resultCommentAttachment1 = (CommentAttachment) resultComment1.getAttachments().toArray()[0];
        CommentAttachment resultCommentAttachment2 = (CommentAttachment) resultComment2.getAttachments().toArray()[0];

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(resultComment1)
                .hasFieldOrPropertyWithValue("text", comment1.getObjects()[1])
                .hasFieldOrPropertyWithValue("createDate", comment1.getObjects()[2])
                .hasFieldOrPropertyWithValue("editDate", comment1.getObjects()[3])
                .hasFieldOrPropertyWithValue("userId", comment1.getObjects()[4])
                .hasFieldOrPropertyWithValue("articleId", comment1.getObjects()[5]);
        soft.assertAll();
        soft.assertThat(resultComment2)
                .hasFieldOrPropertyWithValue("text", comment2.getObjects()[1])
                .hasFieldOrPropertyWithValue("createDate", comment2.getObjects()[2])
                .hasFieldOrPropertyWithValue("editDate", comment2.getObjects()[3])
                .hasFieldOrPropertyWithValue("userId", comment2.getObjects()[4])
                .hasFieldOrPropertyWithValue("articleId", comment2.getObjects()[5]);
        soft.assertAll();
        soft.assertThat(resultComment3)
                .hasFieldOrPropertyWithValue("text", comment3.getObjects()[1])
                .hasFieldOrPropertyWithValue("createDate", comment3.getObjects()[2])
                .hasFieldOrPropertyWithValue("editDate", comment3.getObjects()[3])
                .hasFieldOrPropertyWithValue("userId", comment3.getObjects()[4])
                .hasFieldOrPropertyWithValue("articleId", comment3.getObjects()[5]);
        soft.assertAll();
        soft.assertThat(resultCommentAttachment1)
                .hasFieldOrPropertyWithValue("title", commentAttachment1.getObjects()[1])
                .hasFieldOrPropertyWithValue("path", commentAttachment1.getObjects()[2]);
        soft.assertAll();
        soft.assertThat(resultCommentAttachment2)
                .hasFieldOrPropertyWithValue("title", commentAttachment2.getObjects()[1])
                .hasFieldOrPropertyWithValue("path", commentAttachment2.getObjects()[2]);
        soft.assertAll();
    }

    @DisplayName("Поиск по идентификатору пользователя")
    @Test
    @Transactional
    @Sql(scripts = "classpath:repository-scripts/deployment/comment.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/comment.sql")
    void findByUserId() {
        SoftAssertions soft = new SoftAssertions();
        // статьи
        Comment comment1 = new Comment("Текст 1", createDateComment, editDateComment, 1, 1);
        Comment comment2 = new Comment("Текст 3", createDateComment, editDateComment, 1, 1);
        // прикрепления
        CommentAttachment commentAttachment1 = new CommentAttachment("Прикрепление 1", "/static/attachments/attachment1.png");
        comment1.addNewAttachment(commentAttachment1);
        commentAttachment1.setComment(comment1);

        // получаем список комментариев
        List<Comment> resultComment = commentRepository.findByUserId(1);
        Comment resultComment1 = resultComment.get(0);
        Comment resultComment2 = resultComment.get(1);
        // получаем прикрепления
        CommentAttachment resultCommentAttachment1 = (CommentAttachment) resultComment1.getAttachments().toArray()[0];

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(resultComment1)
                .hasFieldOrPropertyWithValue("text", comment1.getObjects()[1])
                .hasFieldOrPropertyWithValue("createDate", comment1.getObjects()[2])
                .hasFieldOrPropertyWithValue("editDate", comment1.getObjects()[3])
                .hasFieldOrPropertyWithValue("userId", comment1.getObjects()[4])
                .hasFieldOrPropertyWithValue("articleId", comment1.getObjects()[5]);
        soft.assertAll();
        soft.assertThat(resultComment2)
                .hasFieldOrPropertyWithValue("text", comment2.getObjects()[1])
                .hasFieldOrPropertyWithValue("createDate", comment2.getObjects()[2])
                .hasFieldOrPropertyWithValue("editDate", comment2.getObjects()[3])
                .hasFieldOrPropertyWithValue("userId", comment2.getObjects()[4])
                .hasFieldOrPropertyWithValue("articleId", comment2.getObjects()[5]);
        soft.assertAll();
        soft.assertThat(resultCommentAttachment1)
                .hasFieldOrPropertyWithValue("title", commentAttachment1.getObjects()[1])
                .hasFieldOrPropertyWithValue("path", commentAttachment1.getObjects()[2]);
        soft.assertAll();
    }

    @DisplayName("Сохранение сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/comment.sql")
    void saveComment() {
        SoftAssertions soft = new SoftAssertions();
        // статьи
        Comment comment1 = new Comment("Текст 1", createDateComment, editDateComment, 1, 1);
        // прикрепления
        CommentAttachment commentAttachment1 = new CommentAttachment("Прикрепление 1", "/static/attachments/attachment1.png");
        CommentAttachment commentAttachment2 = new CommentAttachment("Прикрепление 2", "/static/attachments/attachment2.png");
        comment1.addNewAttachment(commentAttachment1);
        comment1.addNewAttachment(commentAttachment2);
        commentAttachment1.setComment(comment1);
        commentAttachment2.setComment(comment1);

        Comment result = commentRepository.save(comment1);
        // получаем прикрепления
        CommentAttachment resultCommentAttachment1 = (CommentAttachment) result.getAttachments().toArray()[0];
        CommentAttachment resultCommentAttachment2 = (CommentAttachment) result.getAttachments().toArray()[1];

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("text", comment1.getObjects()[1])
                .hasFieldOrPropertyWithValue("createDate", comment1.getObjects()[2])
                .hasFieldOrPropertyWithValue("editDate", comment1.getObjects()[3])
                .hasFieldOrPropertyWithValue("userId", comment1.getObjects()[4])
                .hasFieldOrPropertyWithValue("articleId", comment1.getObjects()[5]);
        soft.assertAll();
        soft.assertThat(resultCommentAttachment1)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("title", commentAttachment1.getObjects()[1])
                .hasFieldOrPropertyWithValue("path", commentAttachment1.getObjects()[2]);
        soft.assertAll();
        soft.assertThat(resultCommentAttachment2)
                .hasFieldOrPropertyWithValue("id", 2)
                .hasFieldOrPropertyWithValue("title", commentAttachment2.getObjects()[1])
                .hasFieldOrPropertyWithValue("path", commentAttachment2.getObjects()[2]);
        soft.assertAll();
    }

    @DisplayName("Обновление сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/comment.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/comment.sql")
    void updateComment() {
        SoftAssertions soft = new SoftAssertions();
        // статьи
        Comment comment1 = new Comment(2,"Текст 20", createDateComment, editDateComment, 1, 1);
        // прикрепления
        CommentAttachment commentAttachment1 = new CommentAttachment(2, "Прикрепление 20", "/static/attachments/attachment20.png");
        CommentAttachment commentAttachment2 = new CommentAttachment("Прикрепление 3", "/static/attachments/attachment3.png");
        comment1.addNewAttachment(commentAttachment1);
        comment1.addNewAttachment(commentAttachment2);
        commentAttachment1.setComment(comment1);
        commentAttachment2.setComment(comment1);

        Comment result = commentRepository.save(comment1);
        // получаем изображения
        CommentAttachment resultCommentAttachment1 = (CommentAttachment) result.getAttachments().toArray()[0];
        CommentAttachment resultCommentAttachment2 = (CommentAttachment) result.getAttachments().toArray()[1];

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("text", comment1.getObjects()[1])
                .hasFieldOrPropertyWithValue("createDate", comment1.getObjects()[2])
                .hasFieldOrPropertyWithValue("editDate", comment1.getObjects()[3])
                .hasFieldOrPropertyWithValue("userId", comment1.getObjects()[4])
                .hasFieldOrPropertyWithValue("articleId", comment1.getObjects()[5]);
        soft.assertAll();
        soft.assertThat(resultCommentAttachment1)
                .hasFieldOrPropertyWithValue("title", commentAttachment1.getObjects()[1])
                .hasFieldOrPropertyWithValue("path", commentAttachment1.getObjects()[2]);
        soft.assertAll();
        soft.assertThat(resultCommentAttachment2)
                .hasFieldOrPropertyWithValue("title", commentAttachment2.getObjects()[1])
                .hasFieldOrPropertyWithValue("path", commentAttachment2.getObjects()[2]);
        soft.assertAll();
    }

    @DisplayName("Удаление сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/comment.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/comment.sql")
    void deleteComment() {

        commentRepository.deleteById(1);

        assertThat(commentRepository.existsById(1)).as("Запись типа Comment не была удалена").isFalse();
    }
}
