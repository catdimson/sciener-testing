package news.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import news.NewsApplication;
import news.model.Group;
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

@DisplayName("Тестирование контроллера для Group")
@Testcontainers
@SpringBootTest(classes = NewsApplication.class)
@ContextConfiguration(initializers = GroupControllerTest.Initializer.class)
@AutoConfigureMockMvc
class GroupControllerTest {

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
                    "spring.datagroup.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datagroup.password=" + postgreSQLContainer.getPassword(),
                    "spring.datagroup.username=" + postgreSQLContainer.getUsername()
            );
            values.applyTo(configurableApplicationContext);
        }
    }

    @DisplayName("Получение всех сущностей")
    @Sql(scripts = "classpath:repository-scripts/deployment/group.sql")
    @Sql(statements = "INSERT INTO \"group\" (title) VALUES ('Админ'), ('Редактор');")
    @Test
    void findAllGroups() throws Exception {
        this.mockMvc.perform(
                get("/group/")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"id\":1,\"title\":\"Админ\"},{\"id\":2,\"title\":\"Редактор\"}]"));
    }

    @DisplayName("Получение по заголовку")
    @Sql(scripts = "classpath:repository-scripts/deployment/group.sql")
    @Sql(statements = "INSERT INTO \"group\" (title) VALUES ('Админ'), ('Редактор');")
    @Test
    void findGroupsByTitle() throws Exception {
        this.mockMvc.perform(
                get("/group/?title=Админ")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"id\":1,\"title\":\"Админ\"}]"));
    }

    @DisplayName("Получение по ID")
    @Sql(scripts = "classpath:repository-scripts/deployment/group.sql")
    @Sql(statements = "INSERT INTO \"group\" (title) VALUES ('Админ'), ('Редактор');")
    @Test
    void findGroupById() throws Exception {
        this.mockMvc.perform(
                get("/group/2/")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":2,\"title\":\"Редактор\"}"));
    }

    @DisplayName("Создание сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/group.sql")
    @Test
    void createGroup() throws Exception {
        Group group = new Group("SEO");
        this.mockMvc.perform(
                post("/group/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(group))
        )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("Обновление сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/group.sql")
    @Sql(statements = "INSERT INTO \"group\" (title) VALUES ('Админ'), ('Редактор');")
    @Test
    void updateGroup() throws Exception {
        Group group = new Group(2, "SEO");
        this.mockMvc.perform(
                put("/group/2/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(group))
        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @DisplayName("Удаление сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/group.sql")
    @Sql(statements = "INSERT INTO \"group\" (title) VALUES ('Админ'), ('Редактор');")
    @Test
    void deleteGroup() throws Exception {
        this.mockMvc.perform(
                delete("/group/1/")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}