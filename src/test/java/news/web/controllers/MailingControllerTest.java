package news.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import news.NewsApplication;
import news.model.Mailing;
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

@DisplayName("Тестирование контроллера для Mailing")
@Testcontainers
@SpringBootTest(classes = NewsApplication.class)
@ContextConfiguration(initializers = MailingControllerTest.Initializer.class)
@AutoConfigureMockMvc
class MailingControllerTest {

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
                    "spring.datamailing.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datamailing.password=" + postgreSQLContainer.getPassword(),
                    "spring.datamailing.username=" + postgreSQLContainer.getUsername()
            );
            values.applyTo(configurableApplicationContext);
        }
    }

    @DisplayName("Получение всех сущностей")
    @Sql(scripts = "classpath:repository-scripts/deployment/mailing.sql")
    @Sql(statements = "INSERT INTO mailing (email) VALUES ('test1@mail.ru'), ('test2@mail.ru');")
    @Test
    void findAllMailings() throws Exception {
        this.mockMvc.perform(
                get("/mailing/")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"id\":1,\"email\":\"test1@mail.ru\"},{\"id\":2,\"email\":\"test2@mail.ru\"}]"));
    }

    @DisplayName("Получение по email")
    @Sql(scripts = "classpath:repository-scripts/deployment/mailing.sql")
    @Sql(statements = "INSERT INTO mailing (email) VALUES ('test1@mail.ru'), ('test2@mail.ru');")
    @Test
    void findMailingsByEmail() throws Exception {
        this.mockMvc.perform(
                get("/mailing/?email=test1@mail.ru")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"id\":1,\"email\":\"test1@mail.ru\"}]"));
    }

    @DisplayName("Получение по ID")
    @Sql(scripts = "classpath:repository-scripts/deployment/mailing.sql")
    @Sql(statements = "INSERT INTO mailing (email) VALUES ('test1@mail.ru'), ('test2@mail.ru');")
    @Test
    void findMailingById() throws Exception {
        this.mockMvc.perform(
                get("/mailing/2/")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":2,\"email\":\"test2@mail.ru\"}"));
    }

    @DisplayName("Создание сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/mailing.sql")
    @Test
    void createMailing() throws Exception {
        Mailing mailing = new Mailing("test2@mail.ru");
        this.mockMvc.perform(
                post("/mailing/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mailing))
        )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("Обновление сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/mailing.sql")
    @Sql(statements = "INSERT INTO mailing (email) VALUES ('test1@mail.ru'), ('test2@mail.ru');")
    @Test
    void updateMailing() throws Exception {
        Mailing mailing = new Mailing(1, "test3@mail.ru");
        this.mockMvc.perform(
                put("/mailing/1/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mailing))
        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @DisplayName("Удаление сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/mailing.sql")
    @Sql(statements = "INSERT INTO mailing (email) VALUES ('test1@mail.ru'), ('test2@mail.ru');")
    @Test
    void deleteMailing() throws Exception {
        this.mockMvc.perform(
                delete("/mailing/1/")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}