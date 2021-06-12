package news.dao.repositories;

import news.model.Source;
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

@DisplayName("Тестирование репозитория для Source")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = SourceRepositoryTest.Initializer.class)
class SourceRepositoryTest {

    @Autowired
    private SourceRepository sourceRepository;

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
    @Sql(scripts = "classpath:repository-scripts/deployment/source.sql")
    @Sql(statements = "INSERT INTO source (title, url) VALUES ('Яндекс ДЗЕН', 'https://zen.yandex.ru/'), ('РИА', 'https://ria.ru/');")
    void findById() {
        SoftAssertions soft = new SoftAssertions();
        Source source = new Source(1, "Яндекс ДЗЕН", "https://zen.yandex.ru/");

        Source result = sourceRepository.findById(1).get();

        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("id", source.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", source.getObjects()[1])
                .hasFieldOrPropertyWithValue("url", source.getObjects()[2]);
        soft.assertAll();
    }

    @DisplayName("Получение всех записей")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/source.sql")
    @Sql(statements = "INSERT INTO source (title, url) VALUES ('Яндекс ДЗЕН', 'https://zen.yandex.ru/'), ('РИА', 'https://ria.ru/');")
    void findAll() {
        SoftAssertions soft = new SoftAssertions();
        Source source1 = new Source(1, "Яндекс ДЗЕН", "https://zen.yandex.ru/");
        Source source2 = new Source(2, "РИА", "https://ria.ru/");

        List<Source> result = sourceRepository.findAll();

        soft.assertThat(result.get(0))
                .hasFieldOrPropertyWithValue("id", source1.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", source1.getObjects()[1])
                .hasFieldOrPropertyWithValue("url", source1.getObjects()[2]);
        soft.assertAll();
        soft.assertThat(result.get(1))
                .hasFieldOrPropertyWithValue("id", source2.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", source2.getObjects()[1])
                .hasFieldOrPropertyWithValue("url", source2.getObjects()[2]);
        soft.assertAll();
    }

    @DisplayName("Получение записей по title")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/source.sql")
    @Sql(statements = "INSERT INTO source (title, url) VALUES ('Яндекс ДЗЕН', 'https://zen.yandex.ru/'), ('РИА', 'https://ria.ru/'), " +
            "('Яндекс ДЗЕН', 'https://zen2.yandex.ru/');")
    void findByTitle() {
        SoftAssertions soft = new SoftAssertions();
        Source source1 = new Source(1, "Яндекс ДЗЕН", "https://zen.yandex.ru/");
        Source source2 = new Source(3, "Яндекс ДЗЕН", "https://zen2.yandex.ru/");

        List<Source> result = sourceRepository.findByTitle("Яндекс ДЗЕН");

        soft.assertThat(result.get(0))
                .hasFieldOrPropertyWithValue("id", source1.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", source1.getObjects()[1])
                .hasFieldOrPropertyWithValue("url", source1.getObjects()[2]);
        soft.assertAll();
        soft.assertThat(result.get(1))
                .hasFieldOrPropertyWithValue("id", source2.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", source2.getObjects()[1])
                .hasFieldOrPropertyWithValue("url", source2.getObjects()[2]);
        soft.assertAll();
    }

    @DisplayName("Сохранение сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/source.sql")
    void saveSource() {
        SoftAssertions soft = new SoftAssertions();
        Source source = new Source("Яндекс ДЗЕН", "https://zen.yandex.ru/");

        Source result = sourceRepository.save(source);

        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("title", source.getObjects()[1])
                .hasFieldOrPropertyWithValue("url", source.getObjects()[2]);
        soft.assertAll();
    }

    @DisplayName("Обновление сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/source.sql")
    @Sql(statements = "INSERT INTO source (title, url) VALUES ('Яндекс ДЗЕН', 'https://zen.yandex.ru/')")
    void updateSource() {
        SoftAssertions soft = new SoftAssertions();
        Source source = new Source(1, "Яндекс ДЗЕН update", "https://zen2.yandex.ru/");

        Source result = sourceRepository.save(source);

        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("title", source.getObjects()[1])
                .hasFieldOrPropertyWithValue("url", source.getObjects()[2]);
        soft.assertAll();
    }

    @DisplayName("Удаление сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/source.sql")
    @Sql(statements = "INSERT INTO source (title, url) VALUES ('Яндекс ДЗЕН', 'https://zen.yandex.ru/')")
    void deleteSource() {

        sourceRepository.deleteById(1);

        assertThat(sourceRepository.existsById(1)).as("Запись типа Source не была удалена").isFalse();
    }
}
