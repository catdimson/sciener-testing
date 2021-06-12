package news.dao.repositories;

import news.model.Afisha;
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
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тестирование репозитория для Afisha")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = AfishaRepositoryTest.Initializer.class)
class AfishaRepositoryTest {
    private static Timestamp date;

    @BeforeAll
    static void setUp() {
        date = new Timestamp(1563000000000L);
    }

    @Autowired
    private AfishaRepository afishaRepository;

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

    @DisplayName("Получение по ID")
    @Test
    @Transactional
    @Sql(scripts = "classpath:repository-scripts/deployment/afisha.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/afisha.sql")
    void findById() {
        SoftAssertions soft = new SoftAssertions();
        // пользователи
        Afisha afisha1 = new Afisha("title111", "image_url111", "lead111", "desc111", "3", "180",
                "place111", "89205558866", date, false, 1, 1);

        // получение афиши по id
        Optional<Afisha> resultAfisha = afishaRepository.findById(1);
        Afisha resultAfisha1 = resultAfisha.get();

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(resultAfisha1)
                .hasFieldOrPropertyWithValue("title", afisha1.getObjects()[1])
                .hasFieldOrPropertyWithValue("imageUrl", afisha1.getObjects()[2])
                .hasFieldOrPropertyWithValue("lead", afisha1.getObjects()[3])
                .hasFieldOrPropertyWithValue("description", afisha1.getObjects()[4])
                .hasFieldOrPropertyWithValue("ageLimit", afisha1.getObjects()[5])
                .hasFieldOrPropertyWithValue("timing", afisha1.getObjects()[6])
                .hasFieldOrPropertyWithValue("place", afisha1.getObjects()[7])
                .hasFieldOrPropertyWithValue("phone", afisha1.getObjects()[8])
                .hasFieldOrPropertyWithValue("date", afisha1.getObjects()[9])
                .hasFieldOrPropertyWithValue("isCommercial", afisha1.getObjects()[10])
                .hasFieldOrPropertyWithValue("userId", afisha1.getObjects()[11])
                .hasFieldOrPropertyWithValue("sourceId", afisha1.getObjects()[12]);
        soft.assertAll();
    }

    @DisplayName("Получение всех записей")
    @Test
    @Transactional
    @Sql(scripts = "classpath:repository-scripts/deployment/afisha.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/afisha.sql")
    void findAll() {
        SoftAssertions soft = new SoftAssertions();
        // афиши
        Afisha afisha1 = new Afisha("title111", "image_url111", "lead111", "desc111", "3", "180",
                "place111", "89205558866", date, false, 1, 1);
        Afisha afisha2 = new Afisha("title111", "image_url222", "lead222", "desc222", "6", "200",
                "place222", "89205550000", date, false, 2, 1);
        Afisha afisha3 = new Afisha("title333", "image_url333", "lead333", "desc333", "12", "220",
                "place333", "293400", date, false, 2, 1);

        // получаем список афиш
        List<Afisha> resultAfisha = afishaRepository.findAll();
        Afisha resultAfisha1 = resultAfisha.get(0);
        Afisha resultAfisha2 = resultAfisha.get(1);
        Afisha resultAfisha3 = resultAfisha.get(2);

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(resultAfisha1)
                .hasFieldOrPropertyWithValue("title", afisha1.getObjects()[1])
                .hasFieldOrPropertyWithValue("imageUrl", afisha1.getObjects()[2])
                .hasFieldOrPropertyWithValue("lead", afisha1.getObjects()[3])
                .hasFieldOrPropertyWithValue("description", afisha1.getObjects()[4])
                .hasFieldOrPropertyWithValue("ageLimit", afisha1.getObjects()[5])
                .hasFieldOrPropertyWithValue("timing", afisha1.getObjects()[6])
                .hasFieldOrPropertyWithValue("place", afisha1.getObjects()[7])
                .hasFieldOrPropertyWithValue("phone", afisha1.getObjects()[8])
                .hasFieldOrPropertyWithValue("date", afisha1.getObjects()[9])
                .hasFieldOrPropertyWithValue("isCommercial", afisha1.getObjects()[10])
                .hasFieldOrPropertyWithValue("userId", afisha1.getObjects()[11])
                .hasFieldOrPropertyWithValue("sourceId", afisha1.getObjects()[12]);
        soft.assertAll();
        soft.assertThat(resultAfisha2)
                .hasFieldOrPropertyWithValue("title", afisha2.getObjects()[1])
                .hasFieldOrPropertyWithValue("imageUrl", afisha2.getObjects()[2])
                .hasFieldOrPropertyWithValue("lead", afisha2.getObjects()[3])
                .hasFieldOrPropertyWithValue("description", afisha2.getObjects()[4])
                .hasFieldOrPropertyWithValue("ageLimit", afisha2.getObjects()[5])
                .hasFieldOrPropertyWithValue("timing", afisha2.getObjects()[6])
                .hasFieldOrPropertyWithValue("place", afisha2.getObjects()[7])
                .hasFieldOrPropertyWithValue("phone", afisha2.getObjects()[8])
                .hasFieldOrPropertyWithValue("date", afisha2.getObjects()[9])
                .hasFieldOrPropertyWithValue("isCommercial", afisha2.getObjects()[10])
                .hasFieldOrPropertyWithValue("userId", afisha2.getObjects()[11])
                .hasFieldOrPropertyWithValue("sourceId", afisha2.getObjects()[12]);
        soft.assertAll();
        soft.assertThat(resultAfisha3)
                .hasFieldOrPropertyWithValue("title", afisha3.getObjects()[1])
                .hasFieldOrPropertyWithValue("imageUrl", afisha3.getObjects()[2])
                .hasFieldOrPropertyWithValue("lead", afisha3.getObjects()[3])
                .hasFieldOrPropertyWithValue("description", afisha3.getObjects()[4])
                .hasFieldOrPropertyWithValue("ageLimit", afisha3.getObjects()[5])
                .hasFieldOrPropertyWithValue("timing", afisha3.getObjects()[6])
                .hasFieldOrPropertyWithValue("place", afisha3.getObjects()[7])
                .hasFieldOrPropertyWithValue("phone", afisha3.getObjects()[8])
                .hasFieldOrPropertyWithValue("date", afisha3.getObjects()[9])
                .hasFieldOrPropertyWithValue("isCommercial", afisha3.getObjects()[10])
                .hasFieldOrPropertyWithValue("userId", afisha3.getObjects()[11])
                .hasFieldOrPropertyWithValue("sourceId", afisha3.getObjects()[12]);
        soft.assertAll();
    }

    @DisplayName("Поиск по имени")
    @Test
    @Transactional
    @Sql(scripts = "classpath:repository-scripts/deployment/afisha.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/afisha.sql")
    void findByFirstname() {
        SoftAssertions soft = new SoftAssertions();
        // афиши
        Afisha afisha1 = new Afisha("title111", "image_url111", "lead111", "desc111", "3", "180",
                "place111", "89205558866", date, false, 1, 1);
        Afisha afisha2 = new Afisha("title111", "image_url222", "lead222", "desc222", "6", "200",
                "place222", "89205550000", date, false, 2, 1);

        // получаем список афиш
        List<Afisha> resultAfisha = afishaRepository.findByTitle("title111");
        Afisha resultAfisha1 = resultAfisha.get(0);
        Afisha resultAfisha2 = resultAfisha.get(1);

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(resultAfisha1)
                .hasFieldOrPropertyWithValue("title", afisha1.getObjects()[1])
                .hasFieldOrPropertyWithValue("imageUrl", afisha1.getObjects()[2])
                .hasFieldOrPropertyWithValue("lead", afisha1.getObjects()[3])
                .hasFieldOrPropertyWithValue("description", afisha1.getObjects()[4])
                .hasFieldOrPropertyWithValue("ageLimit", afisha1.getObjects()[5])
                .hasFieldOrPropertyWithValue("timing", afisha1.getObjects()[6])
                .hasFieldOrPropertyWithValue("place", afisha1.getObjects()[7])
                .hasFieldOrPropertyWithValue("phone", afisha1.getObjects()[8])
                .hasFieldOrPropertyWithValue("date", afisha1.getObjects()[9])
                .hasFieldOrPropertyWithValue("isCommercial", afisha1.getObjects()[10])
                .hasFieldOrPropertyWithValue("userId", afisha1.getObjects()[11])
                .hasFieldOrPropertyWithValue("sourceId", afisha1.getObjects()[12]);
        soft.assertAll();
        soft.assertThat(resultAfisha2)
                .hasFieldOrPropertyWithValue("title", afisha2.getObjects()[1])
                .hasFieldOrPropertyWithValue("imageUrl", afisha2.getObjects()[2])
                .hasFieldOrPropertyWithValue("lead", afisha2.getObjects()[3])
                .hasFieldOrPropertyWithValue("description", afisha2.getObjects()[4])
                .hasFieldOrPropertyWithValue("ageLimit", afisha2.getObjects()[5])
                .hasFieldOrPropertyWithValue("timing", afisha2.getObjects()[6])
                .hasFieldOrPropertyWithValue("place", afisha2.getObjects()[7])
                .hasFieldOrPropertyWithValue("phone", afisha2.getObjects()[8])
                .hasFieldOrPropertyWithValue("date", afisha2.getObjects()[9])
                .hasFieldOrPropertyWithValue("isCommercial", afisha2.getObjects()[10])
                .hasFieldOrPropertyWithValue("userId", afisha2.getObjects()[11])
                .hasFieldOrPropertyWithValue("sourceId", afisha2.getObjects()[12]);
        soft.assertAll();
    }

    @DisplayName("Сохранение сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/afisha.sql")
    void saveAfisha() {
        SoftAssertions soft = new SoftAssertions();
        // афиши
        Afisha afisha1 = new Afisha("title111", "image_url111", "lead111", "desc111", "3", "180",
                "place111", "89205558866", date, false, 1, 1);

        Afisha result = afishaRepository.save(afisha1);

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("title", afisha1.getObjects()[1])
                .hasFieldOrPropertyWithValue("imageUrl", afisha1.getObjects()[2])
                .hasFieldOrPropertyWithValue("lead", afisha1.getObjects()[3])
                .hasFieldOrPropertyWithValue("description", afisha1.getObjects()[4])
                .hasFieldOrPropertyWithValue("ageLimit", afisha1.getObjects()[5])
                .hasFieldOrPropertyWithValue("timing", afisha1.getObjects()[6])
                .hasFieldOrPropertyWithValue("place", afisha1.getObjects()[7])
                .hasFieldOrPropertyWithValue("phone", afisha1.getObjects()[8])
                .hasFieldOrPropertyWithValue("date", afisha1.getObjects()[9])
                .hasFieldOrPropertyWithValue("isCommercial", afisha1.getObjects()[10])
                .hasFieldOrPropertyWithValue("userId", afisha1.getObjects()[11])
                .hasFieldOrPropertyWithValue("sourceId", afisha1.getObjects()[12]);
        soft.assertAll();
    }

    @DisplayName("Обновление сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/afisha.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/afisha.sql")
    void updateAfisha() {
        SoftAssertions soft = new SoftAssertions();
        // афиши
        Afisha afisha1 = new Afisha(1, "title111_new", "image_url111_new", "lead111_new",
                "desc111_new", "18", "240",
                "place111_new", "89205558866", date, false, 1, 1);

        Afisha result = afishaRepository.save(afisha1);

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("title", afisha1.getObjects()[1])
                .hasFieldOrPropertyWithValue("imageUrl", afisha1.getObjects()[2])
                .hasFieldOrPropertyWithValue("lead", afisha1.getObjects()[3])
                .hasFieldOrPropertyWithValue("description", afisha1.getObjects()[4])
                .hasFieldOrPropertyWithValue("ageLimit", afisha1.getObjects()[5])
                .hasFieldOrPropertyWithValue("timing", afisha1.getObjects()[6])
                .hasFieldOrPropertyWithValue("place", afisha1.getObjects()[7])
                .hasFieldOrPropertyWithValue("phone", afisha1.getObjects()[8])
                .hasFieldOrPropertyWithValue("date", afisha1.getObjects()[9])
                .hasFieldOrPropertyWithValue("isCommercial", afisha1.getObjects()[10])
                .hasFieldOrPropertyWithValue("userId", afisha1.getObjects()[11])
                .hasFieldOrPropertyWithValue("sourceId", afisha1.getObjects()[12]);
        soft.assertAll();
    }

    @DisplayName("Удаление сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/afisha.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/afisha.sql")
    void deleteAfisha() {

        afishaRepository.deleteById(1);

        assertThat(afishaRepository.existsById(1)).as("Запись типа Afisha не была удалена").isFalse();
    }
}
