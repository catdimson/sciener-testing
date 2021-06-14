package news.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import news.NewsApplication;
import news.model.Source;
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

@DisplayName("Тестирование контроллера для Source")
@Testcontainers
@SpringBootTest(classes = NewsApplication.class)
@ContextConfiguration(initializers = SourceControllerTest.Initializer.class)
@AutoConfigureMockMvc
class SourceControllerTest {

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
    @Sql(scripts = "classpath:repository-scripts/deployment/source.sql")
    @Sql(statements = "INSERT INTO source (title, url) VALUES ('Яндекс ДЗЕН', 'https://zen.yandex.ru/'), ('РИА', 'https://ria.ru/');")
    @Test
    void findAllSources() throws Exception {
        this.mockMvc.perform(
                get("/source/")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"id\":1,\"title\":\"Яндекс ДЗЕН\",\"url\":\"https://zen.yandex.ru/\"},{\"id\":2,\"title\":\"РИА\",\"url\":\"https://ria.ru/\"}]"));
    }

    @DisplayName("Получение по заголовку")
    @Sql(scripts = "classpath:repository-scripts/deployment/source.sql")
    @Sql(statements = "INSERT INTO source (title, url) VALUES ('Яндекс ДЗЕН', 'https://zen.yandex.ru/'), ('РИА', 'https://ria.ru/');")
    @Test
    void findSourcesByTitle() throws Exception {
        this.mockMvc.perform(
                get("/source/?title=РИА")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"id\":2,\"title\":\"РИА\",\"url\":\"https://ria.ru/\"}]"));
    }

    @DisplayName("Получение по ID")
    @Sql(scripts = "classpath:repository-scripts/deployment/source.sql")
    @Sql(statements = "INSERT INTO source (title, url) VALUES ('Яндекс ДЗЕН', 'https://zen.yandex.ru/'), ('РИА', 'https://ria.ru/');")
    @Test
    void findSourceById() throws Exception {
        this.mockMvc.perform(
                get("/source/2/")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":2,\"title\":\"РИА\",\"url\":\"https://ria.ru/\"}"));
    }

    @DisplayName("Создание сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/source.sql")
    @Test
    void createSource() throws Exception {
        Source source = new Source("Яндекс ДЗЕН", "https://zen.yandex.ru/");
        this.mockMvc.perform(
                post("/source/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(source))
        )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("Обновление сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/source.sql")
    @Sql(statements = "INSERT INTO source (title, url) VALUES ('Яндекс ДЗЕН', 'https://zen.yandex.ru/'), ('РИА', 'https://ria.ru/');")
    @Test
    void updateSource() throws Exception {
        Source source = new Source(1, "Яндекс ДЗЕН 2", "https://zen.yandex.ru/");
        this.mockMvc.perform(
                put("/source/1/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(source))
        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @DisplayName("Удаление сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/source.sql")
    @Sql(statements = "INSERT INTO source (title, url) VALUES ('Яндекс ДЗЕН', 'https://zen.yandex.ru/'), ('РИА', 'https://ria.ru/');")
    @Test
    void deleteSource() throws Exception {
        this.mockMvc.perform(
                delete("/source/1/")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}