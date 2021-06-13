/*
package news.web.controllers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import news.NewsApplication;
import news.model.Article;
import news.model.ArticleImage;
import news.model.Tag;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Тестирование контроллера для Article")
@Testcontainers
@SpringBootTest(classes = NewsApplication.class)
@ContextConfiguration(initializers = ArticleControllerTest.Initializer.class)
@AutoConfigureMockMvc
class ArticleControllerTest {
    private static Timestamp createDateArticle;
    private static Timestamp editDateArticle;

    @BeforeAll
    static void setUp() {
        createDateArticle = new Timestamp(1561410000000L);
        editDateArticle = new Timestamp(1561410000000L);
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
    @Sql(scripts = "classpath:repository-scripts/deployment/article.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/article.sql")
    @Test
    void findAllArticles() throws Exception {
        this.mockMvc.perform(
                get("/article/")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(new String(Files.readAllBytes(Paths.get("src/test/resources/controllers/json/article_find_all.json")))));
    }

    @DisplayName("Получение всех сущностей")
    @Sql(scripts = "classpath:repository-scripts/deployment/article.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/article.sql")
    @Test
    void findArticlesByTitle() throws Exception {
        this.mockMvc.perform(
                get("/article/?title=Заголовок 1")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(new String(Files.readAllBytes(Paths.get("src/test/resources/controllers/json/article_find_by_title.json")))));
    }

    @DisplayName("Получение по ID")
    @Sql(scripts = "classpath:repository-scripts/deployment/article.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/article.sql")
    @Test
    void findArticleById() throws Exception {
        this.mockMvc.perform(
                get("/article/1/")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(new String(Files.readAllBytes(Paths.get("src/test/resources/controllers/json/article_find_id.json")))));
    }

    @DisplayName("Создание сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/article.sql")
    @Test
    void createArticle() throws Exception {
        // статьи
        Article article = new Article("Заголовок 1", "Лид 1", createDateArticle,
                editDateArticle, "Текст 1", true, 1, 1, 1);
        // изображения
        ArticleImage articleImage1 = new ArticleImage("Изображение 1", "/static/images/image1.png");
        article.addNewImage(articleImage1);
        articleImage1.setArticle(article);
        // тэги
        Tag tag1 = new Tag("Тег 1");
        article.addNewTag(tag1);
        JsonMapper jsonMapper = new JsonMapper();

        tag1.addNewArticle(article);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        System.out.println("objectMapper.writeValueAsString(article): " + objectMapper.writeValueAsString(article));
        this.mockMvc.perform(
                post("/article/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(article))
        )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    */
/*@DisplayName("Обновление сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/tag.sql")
    @Sql(statements = "INSERT INTO tag(title) values ('Балет'), ('Политика');")
    @Test
    void updateArticle() throws Exception {
        Article tag = new Article(1,"UFC");
        this.mockMvc.perform(
                put("/tag/1/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tag))
        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }*//*


    */
/*@DisplayName("Удаление сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/tag.sql")
    @Sql(statements = "INSERT INTO tag(title) values ('Балет'), ('Политика');")
    @Test
    void deleteArticle() throws Exception {
        this.mockMvc.perform(
                delete("/tag/1/")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }*//*

}*/
