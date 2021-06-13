package news.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import news.NewsApplication;
import news.model.User;
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

@DisplayName("Тестирование контроллера для User")
@Testcontainers
@SpringBootTest(classes = NewsApplication.class)
@ContextConfiguration(initializers = UserControllerTest.Initializer.class)
@AutoConfigureMockMvc
class UserControllerTest {
    private static Timestamp lastLogin;
    private static Timestamp dateJoined;

    @BeforeAll
    static void setUp() {
        lastLogin = new Timestamp(1561410000000L);
        dateJoined = new Timestamp(1561410000000L);
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
    @Sql(scripts = "classpath:repository-scripts/deployment/user.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/user.sql")
    @Test
    void findAllUsers() throws Exception {
        this.mockMvc.perform(
                get("/user/")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(new String(Files.readAllBytes(Paths.get("src/test/resources/controllers/json/user_find_all.json")))));
    }

    @DisplayName("Получение сущностей по имени")
    @Sql(scripts = "classpath:repository-scripts/deployment/user.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/user.sql")
    @Test
    void findUsersByFirstname() throws Exception {
        this.mockMvc.perform(
                get("/user/?firstname=Александр")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(new String(Files.readAllBytes(Paths.get("src/test/resources/controllers/json/user_find_by_firstname.json")))));
    }

    @DisplayName("Получение по ID")
    @Sql(scripts = "classpath:repository-scripts/deployment/user.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/user.sql")
    @Test
    void findUserById() throws Exception {
        this.mockMvc.perform(
                get("/user/1/")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(new String(Files.readAllBytes(Paths.get("src/test/resources/controllers/json/user_find_id.json")))));
    }

    @DisplayName("Создание сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/user.sql")
    @Test
    void createUser() throws Exception {
        User user = new User("password111", "user111", "Александр", "Колесников",
                "mail111@mail.ru", lastLogin, dateJoined, true, true, true, 1);
        this.mockMvc.perform(
                post("/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
        )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("Обновление сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/user.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/user.sql")
    @Test
    void updateUser() throws Exception {
        User user = new User("password111_update", "user111_update", "Александр_update", "Колесников_update",
                "mail111@mail.ru", lastLogin, dateJoined, true, true, true, 1);
        this.mockMvc.perform(
                put("/user/1/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @DisplayName("Удаление сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/user.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/user.sql")
    @Test
    void deleteUser() throws Exception {
        this.mockMvc.perform(
                delete("/user/1/")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
