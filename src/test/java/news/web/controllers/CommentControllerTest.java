package news.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import news.NewsApplication;
import news.model.Comment;
import news.model.CommentAttachment;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Тестирование контроллера для Comment")
@Testcontainers
@SpringBootTest(classes = NewsApplication.class)
@ContextConfiguration(initializers = CommentControllerTest.Initializer.class)
@AutoConfigureMockMvc
class CommentControllerTest {
    private static Timestamp createDateComment;
    private static Timestamp editDateComment;

    @BeforeAll
    static void setUp() {
        createDateComment = new Timestamp(1561410000000L);
        editDateComment = new Timestamp(1561410000000L);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    @DisplayName("Получение всех сущностей")
    @Sql(scripts = "classpath:repository-scripts/deployment/comment.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/comment.sql")
    @Test
    void findAllComments() throws Exception {
        this.mockMvc.perform(
                get("/comment/")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(new String(Files.readAllBytes(Paths.get("src/test/resources/controllers/json/comment_find_all.json")))));
    }

    @DisplayName("Получение сущностей по идентификатору пользователя")
    @Sql(scripts = "classpath:repository-scripts/deployment/comment.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/comment.sql")
    @Test
    void findCommentsByUserId() throws Exception {
        this.mockMvc.perform(
                get("/comment/?userid=1")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(new String(Files.readAllBytes(Paths.get("src/test/resources/controllers/json/comment_find_by_user_id.json")))));
    }

    @DisplayName("Получение по ID")
    @Sql(scripts = "classpath:repository-scripts/deployment/comment.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/comment.sql")
    @Test
    void findCommentById() throws Exception {
        this.mockMvc.perform(
                get("/comment/1/")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(new String(Files.readAllBytes(Paths.get("src/test/resources/controllers/json/comment_find_id.json")))));
    }

    @DisplayName("Создание сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/comment.sql")
    @Test
    void createComment() throws Exception {
        Comment comment = new Comment("Текст 1", createDateComment, editDateComment, 1, 1);
        CommentAttachment commentAttachment1 = new CommentAttachment("Прикрепление 1", "/static/attachments/attachment1.png");
        CommentAttachment commentAttachment2 = new CommentAttachment("Прикрепление 2", "/static/attachments/attachment2.png");
        comment.addNewAttachment(commentAttachment1);
        comment.addNewAttachment(commentAttachment2);
        commentAttachment1.setComment(comment);
        commentAttachment2.setComment(comment);
        this.mockMvc.perform(
                post("/comment/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment))
        )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("Обновление сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/comment.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/comment.sql")
    @Test
    void updateComment() throws Exception {
        Comment comment = new Comment(1,"Текст 1", createDateComment, editDateComment, 1, 1);
        CommentAttachment commentAttachment1 = new CommentAttachment("Прикрепление 1", "/static/attachments/attachment1.png");
        CommentAttachment commentAttachment2 = new CommentAttachment("Прикрепление 2", "/static/attachments/attachment2.png");
        comment.addNewAttachment(commentAttachment1);
        comment.addNewAttachment(commentAttachment2);
        commentAttachment1.setComment(comment);
        commentAttachment2.setComment(comment);
        this.mockMvc.perform(
                put("/comment/1/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment))
        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @DisplayName("Удаление сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/comment.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/comment.sql")
    @Test
    void deleteComment() throws Exception {
        this.mockMvc.perform(
                delete("/comment/1/")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
