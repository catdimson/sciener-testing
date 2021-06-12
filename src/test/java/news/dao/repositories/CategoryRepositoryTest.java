package news.dao.repositories;

import news.model.Category;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тестирование репозитория для Category")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = CategoryRepositoryTest.Initializer.class)
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

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

    @DisplayName("Поиск по ID")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/category.sql")
    @Sql(statements = "INSERT INTO category (title) VALUES ('Спорт'), ('Экономика');")
    void findById() {
        SoftAssertions soft = new SoftAssertions();
        Category category = new Category(1, "Спорт");

        Category result = categoryRepository.findById(1).get();

        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("id", category.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", category.getObjects()[1]);
        soft.assertAll();
    }

    @DisplayName("Получение всех записей")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/category.sql")
    @Sql(statements = "INSERT INTO category (title) VALUES ('Спорт'), ('Экономика');")
    void findAll() {
        SoftAssertions soft = new SoftAssertions();
        Category category1 = new Category(1, "Спорт");
        Category category2 = new Category(2, "Экономика");

        List<Category> result = categoryRepository.findAll();

        soft.assertThat(result.get(0))
                .hasFieldOrPropertyWithValue("id", category1.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", category1.getObjects()[1]);
        soft.assertAll();
        soft.assertThat(result.get(1))
                .hasFieldOrPropertyWithValue("id", category2.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", category2.getObjects()[1]);
        soft.assertAll();
    }

    @DisplayName("Получение записей по title")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/category.sql")
    @Sql(statements = "INSERT INTO category (title) VALUES ('Спорт'), ('Экономика'), ('Политика');")
    void findByEmail() {
        SoftAssertions soft = new SoftAssertions();
        Category category1 = new Category(3, "Политика");

        List<Category> result = categoryRepository.findByTitle("Политика");

        soft.assertThat(result.get(0))
                .hasFieldOrPropertyWithValue("id", category1.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", category1.getObjects()[1]);
        soft.assertAll();
    }

    @DisplayName("Сохранение сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/category.sql")
    void saveCategory() {
        SoftAssertions soft = new SoftAssertions();
        Category category = new Category(1, "Политика");

        Category result = categoryRepository.save(category);

        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("title", category.getObjects()[1]);
        soft.assertAll();
    }

    @DisplayName("Обновление сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/category.sql")
    @Sql(statements = "INSERT INTO category (title) VALUES ('Спорт'), ('Экономика'), ('Политика');")
    void updateCategory() {
        SoftAssertions soft = new SoftAssertions();
        Category category = new Category(3, "Наука");

        Category result = categoryRepository.save(category);

        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("id", 3)
                .hasFieldOrPropertyWithValue("title", category.getObjects()[1]);
        soft.assertAll();
    }

    @DisplayName("Удаление сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/category.sql")
    @Sql(statements = "INSERT INTO category (title) VALUES ('Спорт'), ('Экономика'), ('Политика');")
    void deleteCategory() {

        categoryRepository.deleteById(1);

        assertThat(categoryRepository.existsById(1)).as("Запись типа Category не была удалена").isFalse();
    }
}
