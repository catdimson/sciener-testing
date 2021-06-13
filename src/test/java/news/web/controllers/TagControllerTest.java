package news.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import news.NewsApplication;
import news.model.Tag;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Тестирование контроллера для Tag")
@Testcontainers
@SpringBootTest(classes = NewsApplication.class)
@ContextConfiguration(initializers = TagControllerTest.Initializer.class)
@AutoConfigureMockMvc
class TagControllerTest {

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
    @Sql(scripts = "classpath:repository-scripts/deployment/tag.sql")
    @Sql(statements = "INSERT INTO tag(title) values ('Балет'), ('Политика');")
    @Test
    void findAllTags() throws Exception {
        this.mockMvc.perform(
                get("/tag/")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"id\":1,\"title\":\"Балет\"},{\"id\":2,\"title\":\"Политика\"}]"));
    }

    @DisplayName("Получение по заголовку")
    @Sql(scripts = "classpath:repository-scripts/deployment/tag.sql")
    @Sql(statements = "INSERT INTO tag(title) values ('Балет'), ('Политика');")
    @Test
    void findTagsByTitle() throws Exception {
        this.mockMvc.perform(
                get("/tag/?title=Балет")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"id\":1,\"title\":\"Балет\"}]"));
    }

    @DisplayName("Получение по ID")
    @Sql(scripts = "classpath:repository-scripts/deployment/tag.sql")
    @Sql(statements = "INSERT INTO tag(title) values ('Балет'), ('Политика');")
    @Test
    void findTagById() throws Exception {
        this.mockMvc.perform(
                get("/tag/2/")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":2,\"title\":\"Политика\"}"));
    }

    @DisplayName("Создание сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/tag.sql")
    @Test
    void createTag() throws Exception {
        Tag tag = new Tag("UFC");
        this.mockMvc.perform(
                post("/tag/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tag))
        )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("Обновление сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/tag.sql")
    @Sql(statements = "INSERT INTO tag(title) values ('Балет'), ('Политика');")
    @Test
    void updateTag() throws Exception {
        Tag tag = new Tag(1,"UFC");
        this.mockMvc.perform(
                put("/tag/1/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tag))
        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @DisplayName("Удаление сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/tag.sql")
    @Sql(statements = "INSERT INTO tag(title) values ('Балет'), ('Политика');")
    @Test
    void deleteTag() throws Exception {
        this.mockMvc.perform(
                delete("/tag/1/")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}