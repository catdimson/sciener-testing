package news.dao.repositories;

import news.model.Tag;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
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

@DisplayName("Тестирование репозитория для Tag")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = TagRepositoryTest.Initializer.class)
class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Sql(scripts = "classpath:repository-scripts/deployment/tag.sql")
    @BeforeAll
    static void setUp() {

    }

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
    @Sql(statements = "INSERT INTO tag(title) values ('Балет'),('Политика');")
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

    /*@DisplayName("Сохранение сущности")
    @Test
    void saveTag() {
        Tag tag = new Tag("Еще один новый тег");

        tagRepository.save(tag);

        assertNotNull(tag.getTagId());
    }*/
}
