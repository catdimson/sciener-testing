package news.dao.repositories;

import news.model.Tag;
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

@DisplayName("Тестирование репозитория для Tag")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = TagRepositoryTest.Initializer.class)
class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

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

    @DisplayName("Поиск по ID")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/tag.sql")
    @Sql(statements = "INSERT INTO tag(title) values ('Политика');")
    void findById() {
        SoftAssertions soft = new SoftAssertions();
        Tag tag = new Tag(1, "Политика");

        Tag result = tagRepository.findById(tag.getTagId()).get();

        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("id", tag.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", tag.getObjects()[1]);
        soft.assertAll();
    }

    @DisplayName("Получение всех записей")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/tag.sql")
    @Sql(statements = "INSERT INTO tag(title) values ('Балет'), ('Политика');")
    void findAll() {
        SoftAssertions soft = new SoftAssertions();
        Tag tag1 = new Tag(1, "Балет");
        Tag tag2 = new Tag(2, "Политика");

        List<Tag> result = tagRepository.findAll();

        soft.assertThat(result.get(0))
                .hasFieldOrPropertyWithValue("id", tag1.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", tag1.getObjects()[1]);
        soft.assertAll();
        soft.assertThat(result.get(1))
                .hasFieldOrPropertyWithValue("id", tag2.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", tag2.getObjects()[1]);
        soft.assertAll();
    }

    @DisplayName("Получение записей по title")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/tag.sql")
    @Sql(statements = "INSERT INTO tag(title) values ('Балет'), ('Балет');")
    void findByTitle() {
        SoftAssertions soft = new SoftAssertions();
        Tag tag1 = new Tag(1, "Балет");
        Tag tag2 = new Tag(2, "Балет");

        List<Tag> result = tagRepository.findByTitle("Балет");

        soft.assertThat(result.get(0))
                .hasFieldOrPropertyWithValue("id", tag1.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", tag1.getObjects()[1]);
        soft.assertAll();
        soft.assertThat(result.get(1))
                .hasFieldOrPropertyWithValue("id", tag2.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", tag2.getObjects()[1]);
        soft.assertAll();
    }

    @DisplayName("Сохранение сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/tag.sql")
    void saveTag() {
        SoftAssertions soft = new SoftAssertions();
        Tag tag = new Tag("Новый тег");

        Tag result = tagRepository.save(tag);

        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("title", tag.getObjects()[1]);
        soft.assertAll();
    }

    @DisplayName("Обновление сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/tag.sql")
    @Sql(statements = "INSERT INTO tag(title) VALUES ('Политика');")
    void updateTag() {
        SoftAssertions soft = new SoftAssertions();
        Tag tag = new Tag(1, "Балет");

        Tag result = tagRepository.save(tag);

        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("title", tag.getTitle());
        soft.assertAll();
    }

    @DisplayName("Удаление сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/tag.sql")
    @Sql(statements = "INSERT INTO tag(title) VALUES ('Политика');")
    void deleteTag() {
        SoftAssertions soft = new SoftAssertions();

        tagRepository.deleteById(1);

        assertThat(tagRepository.existsById(1)).as("Запись типа Tag не была удалена").isFalse();
    }
}
