package news.dao.repositories;

import news.model.Group;
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

@DisplayName("Тестирование репозитория для Group")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = GroupRepositoryTest.Initializer.class)
class GroupRepositoryTest {

    @Autowired
    private GroupRepository groupRepository;

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

    @DisplayName("Поиск по ID")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/group.sql")
    @Sql(statements = "INSERT INTO \"group\" (title) VALUES ('Админ'), ('Редактор');")
    void findById() {
        SoftAssertions soft = new SoftAssertions();
        Group group = new Group(1, "Админ");

        Group result = groupRepository.findById(1).get();

        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("id", group.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", group.getObjects()[1]);
        soft.assertAll();
    }

    @DisplayName("Получение всех записей")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/group.sql")
    @Sql(statements = "INSERT INTO \"group\" (title) VALUES ('Админ'), ('Редактор');")
    void findAll() {
        SoftAssertions soft = new SoftAssertions();
        Group group1 = new Group(1, "Админ");
        Group group2 = new Group(2, "Редактор");

        List<Group> result = groupRepository.findAll();

        soft.assertThat(result.get(0))
                .hasFieldOrPropertyWithValue("id", group1.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", group1.getObjects()[1]);
        soft.assertAll();
        soft.assertThat(result.get(1))
                .hasFieldOrPropertyWithValue("id", group2.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", group2.getObjects()[1]);
        soft.assertAll();
    }

    @DisplayName("Получение записей по title")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/group.sql")
    @Sql(statements = "INSERT INTO \"group\" (title) VALUES ('Админ'), ('Редактор'), ('Пользователь');")
    void findByEmail() {
        SoftAssertions soft = new SoftAssertions();
        Group group1 = new Group(3, "Пользователь");

        List<Group> result = groupRepository.findByTitle("Пользователь");

        soft.assertThat(result.get(0))
                .hasFieldOrPropertyWithValue("id", group1.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", group1.getObjects()[1]);
        soft.assertAll();
    }

    @DisplayName("Сохранение сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/group.sql")
    void saveGroup() {
        SoftAssertions soft = new SoftAssertions();
        Group group = new Group(1, "Админ");

        Group result = groupRepository.save(group);

        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("title", group.getObjects()[1]);
        soft.assertAll();
    }

    @DisplayName("Обновление сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/group.sql")
    @Sql(statements = "INSERT INTO \"group\" (title) VALUES ('Админ'), ('Редактор'), ('Пользователь');")
    void updateGroup() {
        SoftAssertions soft = new SoftAssertions();
        Group group = new Group(3, "SEO");

        Group result = groupRepository.save(group);

        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("id", 3)
                .hasFieldOrPropertyWithValue("title", group.getObjects()[1]);
        soft.assertAll();
    }

    @DisplayName("Удаление сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/group.sql")
    @Sql(statements = "INSERT INTO \"group\" (title) VALUES ('Админ'), ('Редактор'), ('Пользователь');")
    void deleteGroup() {

        groupRepository.deleteById(1);

        assertThat(groupRepository.existsById(1)).as("Запись типа Group не была удалена").isFalse();
    }
}
