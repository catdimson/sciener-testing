package news.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import news.NewsApplication;
import news.model.Afisha;
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

@DisplayName("Тестирование контроллера для Afisha")
@Testcontainers
@SpringBootTest(classes = NewsApplication.class)
@ContextConfiguration(initializers = AfishaControllerTest.Initializer.class)
@AutoConfigureMockMvc
class AfishaControllerTest {
    private static Timestamp date;

    @BeforeAll
    static void setUp() {
        date = new Timestamp(1561410000000L);
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
    @Sql(scripts = "classpath:repository-scripts/deployment/afisha.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/afisha.sql")
    @Test
    void findAllAfishas() throws Exception {
        this.mockMvc.perform(
                get("/afisha/")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(new String(Files.readAllBytes(Paths.get("src/test/resources/controllers/json/afisha_find_all.json")))));
    }

    @DisplayName("Получение сущностей по заголовку")
    @Sql(scripts = "classpath:repository-scripts/deployment/afisha.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/afisha.sql")
    @Test
    void findAfishasByFirstname() throws Exception {
        this.mockMvc.perform(
                get("/afisha/?title=title111")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(new String(Files.readAllBytes(Paths.get("src/test/resources/controllers/json/afisha_find_by_title.json")))));
    }

    @DisplayName("Получение по ID")
    @Sql(scripts = "classpath:repository-scripts/deployment/afisha.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/afisha.sql")
    @Test
    void findAfishaById() throws Exception {
        this.mockMvc.perform(
                get("/afisha/1/")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(new String(Files.readAllBytes(Paths.get("src/test/resources/controllers/json/afisha_find_id.json")))));
    }

    @DisplayName("Создание сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/afisha.sql")
    @Test
    void createAfisha() throws Exception {
        // афиши
        Afisha afisha = new Afisha("title111", "image_url111", "lead111", "desc111", "3", "180",
                "place111", "89205558866", date, false, 1, 1);
        this.mockMvc.perform(
                post("/afisha/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(afisha))
        )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("Обновление сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/afisha.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/afisha.sql")
    @Test
    void updateAfisha() throws Exception {
        Afisha afisha = new Afisha(1,"title111_update", "image_url111_update", "lead111_update", "desc111_update",
                "3", "180", "place111", "89205558866", date, false, 1, 1);
        this.mockMvc.perform(
                put("/afisha/1/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(afisha))
        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @DisplayName("Удаление сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/afisha.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/afisha.sql")
    @Test
    void deleteAfisha() throws Exception {
        this.mockMvc.perform(
                delete("/afisha/1/")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
