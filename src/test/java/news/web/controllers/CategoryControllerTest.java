package news.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import news.NewsApplication;
import news.model.Category;
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

@DisplayName("Тестирование контроллера для Category")
@Testcontainers
@SpringBootTest(classes = NewsApplication.class)
@ContextConfiguration(initializers = CategoryControllerTest.Initializer.class)
@AutoConfigureMockMvc
class CategoryControllerTest {

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
                    "spring.datacategory.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datacategory.password=" + postgreSQLContainer.getPassword(),
                    "spring.datacategory.username=" + postgreSQLContainer.getUsername()
            );
            values.applyTo(configurableApplicationContext);
        }
    }

    @DisplayName("Получение всех сущностей")
    @Sql(scripts = "classpath:repository-scripts/deployment/category.sql")
    @Sql(statements = "INSERT INTO category (title) VALUES ('Спорт'), ('Наука');")
    @Test
    void findAllCategorys() throws Exception {
        this.mockMvc.perform(
                get("/category/")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"id\":1,\"title\":\"Спорт\"},{\"id\":2,\"title\":\"Наука\"}]"));
    }

    @DisplayName("Получение по заголовку")
    @Sql(scripts = "classpath:repository-scripts/deployment/category.sql")
    @Sql(statements = "INSERT INTO category (title) VALUES ('Спорт'), ('Наука');")
    @Test
    void findCategorysByTitle() throws Exception {
        this.mockMvc.perform(
                get("/category/?title=Спорт")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"id\":1,\"title\":\"Спорт\"}]"));
    }

    @DisplayName("Получение по ID")
    @Sql(scripts = "classpath:repository-scripts/deployment/category.sql")
    @Sql(statements = "INSERT INTO category (title) VALUES ('Спорт'), ('Наука');")
    @Test
    void findCategoryById() throws Exception {
        this.mockMvc.perform(
                get("/category/2/")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":2,\"title\":\"Наука\"}"));
    }

    @DisplayName("Создание сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/category.sql")
    @Test
    void createCategory() throws Exception {
        Category category = new Category("Политика");
        this.mockMvc.perform(
                post("/category/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category))
        )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("Обновление сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/category.sql")
    @Sql(statements = "INSERT INTO category (title) VALUES ('Спорт'), ('Наука');")
    @Test
    void updateCategory() throws Exception {
        Category category = new Category(2, "Политика");
        this.mockMvc.perform(
                put("/category/2/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category))
        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @DisplayName("Удаление сущности")
    @Sql(scripts = "classpath:repository-scripts/deployment/category.sql")
    @Sql(statements = "INSERT INTO category (title) VALUES ('Админ'), ('Редактор');")
    @Test
    void deleteCategory() throws Exception {
        this.mockMvc.perform(
                delete("/category/1/")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}