package news.dao.repositories;

import news.model.Mailing;
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

@DisplayName("Тестирование репозитория для Mailing")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = MailingRepositoryTest.Initializer.class)
class MailingRepositoryTest {

    @Autowired
    private MailingRepository mailingRepository;

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

    @DisplayName("Поиск по ID")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/mailing.sql")
    @Sql(statements = "INSERT INTO mailing (email) VALUES ('test111@mail.ru'), ('test222@mail.ru');")
    void findById() {
        SoftAssertions soft = new SoftAssertions();
        Mailing mailing = new Mailing(1, "test111@mail.ru");

        Mailing result = mailingRepository.findById(1).get();

        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("id", mailing.getObjects()[0])
                .hasFieldOrPropertyWithValue("email", mailing.getObjects()[1]);
        soft.assertAll();
    }

    @DisplayName("Получение всех записей")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/mailing.sql")
    @Sql(statements = "INSERT INTO mailing (email) VALUES ('test111@mail.ru'), ('test222@mail.ru');")
    void findAll() {
        SoftAssertions soft = new SoftAssertions();
        Mailing mailing1 = new Mailing(1, "test111@mail.ru");
        Mailing mailing2 = new Mailing(2, "test222@mail.ru");

        List<Mailing> result = mailingRepository.findAll();

        soft.assertThat(result.get(0))
                .hasFieldOrPropertyWithValue("id", mailing1.getObjects()[0])
                .hasFieldOrPropertyWithValue("email", mailing1.getObjects()[1]);
        soft.assertAll();
        soft.assertThat(result.get(1))
                .hasFieldOrPropertyWithValue("id", mailing2.getObjects()[0])
                .hasFieldOrPropertyWithValue("email", mailing2.getObjects()[1]);
        soft.assertAll();
    }

    @DisplayName("Получение записей по email")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/mailing.sql")
    @Sql(statements = "INSERT INTO mailing (email) VALUES ('test111@mail.ru'), ('test222@mail.ru'), ('test333@mail.ru');")
    void findByEmail() {
        SoftAssertions soft = new SoftAssertions();
        Mailing mailing1 = new Mailing(1, "test111@mail.ru");

        List<Mailing> result = mailingRepository.findByEmail("test111@mail.ru");

        soft.assertThat(result.get(0))
                .hasFieldOrPropertyWithValue("id", mailing1.getObjects()[0])
                .hasFieldOrPropertyWithValue("email", mailing1.getObjects()[1]);
        soft.assertAll();
    }

    @DisplayName("Сохранение сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/mailing.sql")
    void saveMailing() {
        SoftAssertions soft = new SoftAssertions();
        Mailing mailing = new Mailing("test111@mail.ru");

        Mailing result = mailingRepository.save(mailing);

        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("email", mailing.getObjects()[1]);
        soft.assertAll();
    }

    @DisplayName("Обновление сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/mailing.sql")
    @Sql(statements = "INSERT INTO mailing (email) VALUES ('test111@mail.ru'), ('test222@mail.ru');")
    void updateMailing() {
        SoftAssertions soft = new SoftAssertions();
        Mailing mailing = new Mailing(1, "test111_update@mail.ru");

        Mailing result = mailingRepository.save(mailing);

        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("email", mailing.getObjects()[1]);
        soft.assertAll();
    }

    @DisplayName("Удаление сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/mailing.sql")
    @Sql(statements = "INSERT INTO mailing (email) VALUES ('test111@mail.ru');")
    void deleteMailing() {

        mailingRepository.deleteById(1);

        assertThat(mailingRepository.existsById(1)).as("Запись типа Mailing не была удалена").isFalse();
    }
}
