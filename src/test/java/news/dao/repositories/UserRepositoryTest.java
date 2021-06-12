package news.dao.repositories;

import news.model.User;
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

@DisplayName("Тестирование репозитория для User")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = UserRepositoryTest.Initializer.class)
class UserRepositoryTest {
    private static Timestamp dateJoined;
    private static Timestamp lastLogin;

    @BeforeAll
    static void setUp() {
        dateJoined = new Timestamp(1560000000000L);
        lastLogin = new Timestamp(1563000000000L);
    }

    @Autowired
    private UserRepository userRepository;

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
    @Sql(scripts = "classpath:repository-scripts/deployment/user.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/user.sql")
    void findById() {
        SoftAssertions soft = new SoftAssertions();
        // пользователи
        User user1 = new User("password111", "user111", "Александр", "Колесников",
                "mail111@mail.ru", lastLogin, dateJoined, true, true, true, 1);

        // получаем список статей
        Optional<User> resultUser = userRepository.findById(1);
        User resultUser1 = resultUser.get();

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(resultUser1)
                .hasFieldOrPropertyWithValue("password", user1.getObjects()[1])
                .hasFieldOrPropertyWithValue("username", user1.getObjects()[2])
                .hasFieldOrPropertyWithValue("firstName", user1.getObjects()[3])
                .hasFieldOrPropertyWithValue("lastName", user1.getObjects()[4])
                .hasFieldOrPropertyWithValue("email", user1.getObjects()[5])
                .hasFieldOrPropertyWithValue("lastLogin", user1.getObjects()[6])
                .hasFieldOrPropertyWithValue("dateJoined", user1.getObjects()[7])
                .hasFieldOrPropertyWithValue("isSuperuser", user1.getObjects()[8])
                .hasFieldOrPropertyWithValue("isStaff", user1.getObjects()[9])
                .hasFieldOrPropertyWithValue("isActive", user1.getObjects()[10])
                .hasFieldOrPropertyWithValue("groupId", user1.getObjects()[11]);
        soft.assertAll();
    }

    @DisplayName("Получение всех записей")
    @Test
    @Transactional
    @Sql(scripts = "classpath:repository-scripts/deployment/user.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/user.sql")
    void findAll() {
        SoftAssertions soft = new SoftAssertions();
        // пользователи
        User user1 = new User("password111", "user111", "Александр", "Колесников",
                "mail111@mail.ru", lastLogin, dateJoined, true, true, true, 1);
        User user2 = new User("password222", "user222", "Александр", "Вениаминов",
                "mail222@mail.ru", lastLogin, dateJoined, true, true, false, 1);
        User user3 = new User("password333", "user333", "Максим", "Шаповалов",
                "mail333@mail.ru", lastLogin, dateJoined, false, false, false, 2);

        // получаем список пользователей
        List<User> resultUser = userRepository.findAll();
        User resultUser1 = resultUser.get(0);
        User resultUser2 = resultUser.get(1);
        User resultUser3 = resultUser.get(2);

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(resultUser1)
                .hasFieldOrPropertyWithValue("password", user1.getObjects()[1])
                .hasFieldOrPropertyWithValue("username", user1.getObjects()[2])
                .hasFieldOrPropertyWithValue("firstName", user1.getObjects()[3])
                .hasFieldOrPropertyWithValue("lastName", user1.getObjects()[4])
                .hasFieldOrPropertyWithValue("email", user1.getObjects()[5])
                .hasFieldOrPropertyWithValue("lastLogin", user1.getObjects()[6])
                .hasFieldOrPropertyWithValue("dateJoined", user1.getObjects()[7])
                .hasFieldOrPropertyWithValue("isSuperuser", user1.getObjects()[8])
                .hasFieldOrPropertyWithValue("isStaff", user1.getObjects()[9])
                .hasFieldOrPropertyWithValue("isActive", user1.getObjects()[10])
                .hasFieldOrPropertyWithValue("groupId", user1.getObjects()[11]);
        soft.assertAll();
        soft.assertThat(resultUser2)
                .hasFieldOrPropertyWithValue("password", user2.getObjects()[1])
                .hasFieldOrPropertyWithValue("username", user2.getObjects()[2])
                .hasFieldOrPropertyWithValue("firstName", user2.getObjects()[3])
                .hasFieldOrPropertyWithValue("lastName", user2.getObjects()[4])
                .hasFieldOrPropertyWithValue("email", user2.getObjects()[5])
                .hasFieldOrPropertyWithValue("lastLogin", user2.getObjects()[6])
                .hasFieldOrPropertyWithValue("dateJoined", user2.getObjects()[7])
                .hasFieldOrPropertyWithValue("isSuperuser", user2.getObjects()[8])
                .hasFieldOrPropertyWithValue("isStaff", user2.getObjects()[9])
                .hasFieldOrPropertyWithValue("isActive", user2.getObjects()[10])
                .hasFieldOrPropertyWithValue("groupId", user2.getObjects()[11]);
        soft.assertAll();
        soft.assertThat(resultUser3)
                .hasFieldOrPropertyWithValue("password", user3.getObjects()[1])
                .hasFieldOrPropertyWithValue("username", user3.getObjects()[2])
                .hasFieldOrPropertyWithValue("firstName", user3.getObjects()[3])
                .hasFieldOrPropertyWithValue("lastName", user3.getObjects()[4])
                .hasFieldOrPropertyWithValue("email", user3.getObjects()[5])
                .hasFieldOrPropertyWithValue("lastLogin", user3.getObjects()[6])
                .hasFieldOrPropertyWithValue("dateJoined", user3.getObjects()[7])
                .hasFieldOrPropertyWithValue("isSuperuser", user3.getObjects()[8])
                .hasFieldOrPropertyWithValue("isStaff", user3.getObjects()[9])
                .hasFieldOrPropertyWithValue("isActive", user3.getObjects()[10])
                .hasFieldOrPropertyWithValue("groupId", user3.getObjects()[11]);
        soft.assertAll();
    }

    @DisplayName("Поиск по имени")
    @Test
    @Transactional
    @Sql(scripts = "classpath:repository-scripts/deployment/user.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/user.sql")
    void findByFirstname() {
        SoftAssertions soft = new SoftAssertions();
        // пользователи
        User user1 = new User("password111", "user111", "Александр", "Колесников",
                "mail111@mail.ru", lastLogin, dateJoined, true, true, true, 1);
        User user2 = new User("password222", "user222", "Александр", "Вениаминов",
                "mail222@mail.ru", lastLogin, dateJoined, true, true, false, 1);

        // получаем список пользователей
        List<User> resultUser = userRepository.findByFirstName("Александр");
        User resultUser1 = resultUser.get(0);
        User resultUser2 = resultUser.get(1);

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(resultUser1)
                .hasFieldOrPropertyWithValue("password", user1.getObjects()[1])
                .hasFieldOrPropertyWithValue("username", user1.getObjects()[2])
                .hasFieldOrPropertyWithValue("firstName", user1.getObjects()[3])
                .hasFieldOrPropertyWithValue("lastName", user1.getObjects()[4])
                .hasFieldOrPropertyWithValue("email", user1.getObjects()[5])
                .hasFieldOrPropertyWithValue("lastLogin", user1.getObjects()[6])
                .hasFieldOrPropertyWithValue("dateJoined", user1.getObjects()[7])
                .hasFieldOrPropertyWithValue("isSuperuser", user1.getObjects()[8])
                .hasFieldOrPropertyWithValue("isStaff", user1.getObjects()[9])
                .hasFieldOrPropertyWithValue("isActive", user1.getObjects()[10])
                .hasFieldOrPropertyWithValue("groupId", user1.getObjects()[11]);
        soft.assertAll();
        soft.assertThat(resultUser2)
                .hasFieldOrPropertyWithValue("password", user2.getObjects()[1])
                .hasFieldOrPropertyWithValue("username", user2.getObjects()[2])
                .hasFieldOrPropertyWithValue("firstName", user2.getObjects()[3])
                .hasFieldOrPropertyWithValue("lastName", user2.getObjects()[4])
                .hasFieldOrPropertyWithValue("email", user2.getObjects()[5])
                .hasFieldOrPropertyWithValue("lastLogin", user2.getObjects()[6])
                .hasFieldOrPropertyWithValue("dateJoined", user2.getObjects()[7])
                .hasFieldOrPropertyWithValue("isSuperuser", user2.getObjects()[8])
                .hasFieldOrPropertyWithValue("isStaff", user2.getObjects()[9])
                .hasFieldOrPropertyWithValue("isActive", user2.getObjects()[10])
                .hasFieldOrPropertyWithValue("groupId", user2.getObjects()[11]);
        soft.assertAll();
    }

    @DisplayName("Сохранение сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/user.sql")
    void saveUser() {
        SoftAssertions soft = new SoftAssertions();
        // пользователи
        User user1 = new User("password111", "user111", "Александр", "Колесников",
                "mail111@mail.ru", lastLogin, dateJoined, true, true, true, 1);

        User result = userRepository.save(user1);

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("password", user1.getObjects()[1])
                .hasFieldOrPropertyWithValue("username", user1.getObjects()[2])
                .hasFieldOrPropertyWithValue("firstName", user1.getObjects()[3])
                .hasFieldOrPropertyWithValue("lastName", user1.getObjects()[4])
                .hasFieldOrPropertyWithValue("email", user1.getObjects()[5])
                .hasFieldOrPropertyWithValue("lastLogin", user1.getObjects()[6])
                .hasFieldOrPropertyWithValue("dateJoined", user1.getObjects()[7])
                .hasFieldOrPropertyWithValue("isSuperuser", user1.getObjects()[8])
                .hasFieldOrPropertyWithValue("isStaff", user1.getObjects()[9])
                .hasFieldOrPropertyWithValue("isActive", user1.getObjects()[10])
                .hasFieldOrPropertyWithValue("groupId", user1.getObjects()[11]);
        soft.assertAll();
    }

    @DisplayName("Обновление сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/user.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/user.sql")
    void updateUser() {
        SoftAssertions soft = new SoftAssertions();
        // пользователи
        User user1 = new User(1, "password111_new", "user111_new", "Александр_new", "Колесников_new",
                "mail111_new@mail.ru", lastLogin, dateJoined, true, true, true, 1);

        User result = userRepository.save(user1);

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("password", user1.getObjects()[1])
                .hasFieldOrPropertyWithValue("username", user1.getObjects()[2])
                .hasFieldOrPropertyWithValue("firstName", user1.getObjects()[3])
                .hasFieldOrPropertyWithValue("lastName", user1.getObjects()[4])
                .hasFieldOrPropertyWithValue("email", user1.getObjects()[5])
                .hasFieldOrPropertyWithValue("lastLogin", user1.getObjects()[6])
                .hasFieldOrPropertyWithValue("dateJoined", user1.getObjects()[7])
                .hasFieldOrPropertyWithValue("isSuperuser", user1.getObjects()[8])
                .hasFieldOrPropertyWithValue("isStaff", user1.getObjects()[9])
                .hasFieldOrPropertyWithValue("isActive", user1.getObjects()[10])
                .hasFieldOrPropertyWithValue("groupId", user1.getObjects()[11]);
        soft.assertAll();
    }

    @DisplayName("Удаление сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/user.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/user.sql")
    void deleteUser() {

        userRepository.deleteById(1);

        assertThat(userRepository.existsById(1)).as("Запись типа User не была удалена").isFalse();
    }
}
