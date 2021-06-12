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

    /*@DisplayName("Получение по ID")
    @Test
    @Transactional
    @Sql(scripts = "classpath:repository-scripts/deployment/user.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/user.sql")
    void findById() {
        SoftAssertions soft = new SoftAssertions();
        // статьи
        User user1 = new User(1, "Заголовок 1", "Лид 1", createDateUser,
                editDateUser, "Текст 1", true, 1, 1, 1);
        // изображения
        UserImage userImage1 = new UserImage("Изображение 1", "/static/images/image1.png");
        user1.addNewImage(userImage1);
        userImage1.setUser(user1);
        // тэги
        Tag tag1 = new Tag("Тег 1");
        user1.addNewTag(tag1);
        tag1.addNewUser(user1);

        // получаем список статей
        Optional<User> resultUser = userRepository.findById(1);
        User resultUser1 = resultUser.get();
        // получаем изображения
        UserImage resultUserImage1 = (UserImage) resultUser1.getImages().toArray()[0];
        // получаем теги
        Tag resultTag1 = (Tag) resultUser1.getTags().toArray()[0];

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(resultUser1)
                .hasFieldOrPropertyWithValue("title", user1.getObjects()[1])
                .hasFieldOrPropertyWithValue("lead", user1.getObjects()[2])
                .hasFieldOrPropertyWithValue("createDate", user1.getObjects()[3])
                .hasFieldOrPropertyWithValue("editDate", user1.getObjects()[4])
                .hasFieldOrPropertyWithValue("text", user1.getObjects()[5])
                .hasFieldOrPropertyWithValue("isPublished", user1.getObjects()[6])
                .hasFieldOrPropertyWithValue("userId", user1.getObjects()[7])
                .hasFieldOrPropertyWithValue("sourceId", user1.getObjects()[8]);
        soft.assertAll();
        soft.assertThat(resultUserImage1)
                .hasFieldOrPropertyWithValue("title", userImage1.getObjects()[1])
                .hasFieldOrPropertyWithValue("path", userImage1.getObjects()[2]);
        soft.assertAll();
        assertThat(resultTag1).hasFieldOrPropertyWithValue("title", tag1.getObjects()[1]);
    }*/

    /*@DisplayName("Получение всех записей")
    @Test
    @Transactional
    @Sql(scripts = "classpath:repository-scripts/deployment/user.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/user.sql")
    void findAll() {
        SoftAssertions soft = new SoftAssertions();
        // статьи
        User user1 = new User("Заголовок 1", "Лид 1", createDateUser,
                editDateUser, "Текст 1", true, 1, 1, 1);
        User user2 = new User("Заголовок 1", "Лид 2", createDateUser,
                editDateUser, "Текст 2", true, 2, 2, 2);
        User user3 = new User("Заголовок 3", "Лид 3", createDateUser,
                editDateUser, "Текст 3", true, 2, 2, 2);

        // изображения
        UserImage userImage1 = new UserImage("Изображение 1", "/static/images/image1.png");
        UserImage userImage2 = new UserImage("Изображение 2", "/static/images/image2.png");
        user1.addNewImage(userImage1);
        user2.addNewImage(userImage2);
        userImage1.setUser(user1);
        userImage2.setUser(user2);
        // тэги
        Tag tag1 = new Tag("Тег 1");
        Tag tag2 = new Tag("Тег 2");
        user1.addNewTag(tag1);
        user2.addNewTag(tag2);
        tag1.addNewUser(user1);
        tag2.addNewUser(user2);

        // получаем список статей
        List<User> resultUser = userRepository.findAll();
        User resultUser1 = resultUser.get(0);
        User resultUser2 = resultUser.get(1);
        User resultUser3 = resultUser.get(2);
        // получаем изображения
        UserImage resultUserImage1 = (UserImage) resultUser1.getImages().toArray()[0];
        UserImage resultUserImage2 = (UserImage) resultUser2.getImages().toArray()[0];
        // получаем теги
        Tag resultTag1 = (Tag) resultUser1.getTags().toArray()[0];
        Tag resultTag2 = (Tag) resultUser2.getTags().toArray()[0];

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(resultUser1)
                .hasFieldOrPropertyWithValue("title", user1.getObjects()[1])
                .hasFieldOrPropertyWithValue("lead", user1.getObjects()[2])
                .hasFieldOrPropertyWithValue("createDate", user1.getObjects()[3])
                .hasFieldOrPropertyWithValue("editDate", user1.getObjects()[4])
                .hasFieldOrPropertyWithValue("text", user1.getObjects()[5])
                .hasFieldOrPropertyWithValue("isPublished", user1.getObjects()[6])
                .hasFieldOrPropertyWithValue("userId", user1.getObjects()[7])
                .hasFieldOrPropertyWithValue("sourceId", user1.getObjects()[8]);
        soft.assertAll();
        soft.assertThat(resultUser2)
                .hasFieldOrPropertyWithValue("title", user2.getObjects()[1])
                .hasFieldOrPropertyWithValue("lead", user2.getObjects()[2])
                .hasFieldOrPropertyWithValue("createDate", user2.getObjects()[3])
                .hasFieldOrPropertyWithValue("editDate", user2.getObjects()[4])
                .hasFieldOrPropertyWithValue("text", user2.getObjects()[5])
                .hasFieldOrPropertyWithValue("isPublished", user2.getObjects()[6])
                .hasFieldOrPropertyWithValue("userId", user2.getObjects()[7])
                .hasFieldOrPropertyWithValue("sourceId", user2.getObjects()[8]);
        soft.assertAll();
        soft.assertThat(resultUser3)
                .hasFieldOrPropertyWithValue("title", user3.getObjects()[1])
                .hasFieldOrPropertyWithValue("lead", user3.getObjects()[2])
                .hasFieldOrPropertyWithValue("createDate", user3.getObjects()[3])
                .hasFieldOrPropertyWithValue("editDate", user3.getObjects()[4])
                .hasFieldOrPropertyWithValue("text", user3.getObjects()[5])
                .hasFieldOrPropertyWithValue("isPublished", user3.getObjects()[6])
                .hasFieldOrPropertyWithValue("userId", user3.getObjects()[7])
                .hasFieldOrPropertyWithValue("sourceId", user3.getObjects()[8]);
        soft.assertAll();
        soft.assertThat(resultUserImage1)
                .hasFieldOrPropertyWithValue("title", userImage1.getObjects()[1])
                .hasFieldOrPropertyWithValue("path", userImage1.getObjects()[2]);
        soft.assertAll();
        soft.assertThat(resultUserImage2)
                .hasFieldOrPropertyWithValue("title", userImage2.getObjects()[1])
                .hasFieldOrPropertyWithValue("path", userImage2.getObjects()[2]);
        soft.assertAll();
        assertThat(resultTag1).hasFieldOrPropertyWithValue("title", tag1.getObjects()[1]);
        assertThat(resultTag2).hasFieldOrPropertyWithValue("title", tag2.getObjects()[1]);
    }*/

    @DisplayName("Поиск по имени")
    @Test
    @Transactional
    @Sql(scripts = "classpath:repository-scripts/deployment/user.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/user.sql")
    void findByFirstname() {
        SoftAssertions soft = new SoftAssertions();
        // статьи
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

    /*@DisplayName("Сохранение сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/user.sql")
    void saveUser() {
        SoftAssertions soft = new SoftAssertions();
        // статьи
        User user1 = new User(1, "Заголовок 1", "Лид 1", createDateUser,
                editDateUser, "Текст 1", true, 1, 1, 1);
        // изображения
        UserImage userImage1 = new UserImage(1, "Изображение 1", "/static/images/image1.png");
        user1.addNewImage(userImage1);
        userImage1.setUser(user1);
        // тэги
        Tag tag1 = new Tag(1, "Тег 1");
        user1.addNewTag(tag1);
        tag1.addNewUser(user1);

        User result = userRepository.save(user1);
        // получаем изображения
        UserImage resultUserImage1 = (UserImage) result.getImages().toArray()[0];
        // получаем теги
        Tag resultTag1 = (Tag) result.getTags().toArray()[0];

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("id", user1.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", user1.getObjects()[1])
                .hasFieldOrPropertyWithValue("lead", user1.getObjects()[2])
                .hasFieldOrPropertyWithValue("createDate", user1.getObjects()[3])
                .hasFieldOrPropertyWithValue("editDate", user1.getObjects()[4])
                .hasFieldOrPropertyWithValue("text", user1.getObjects()[5])
                .hasFieldOrPropertyWithValue("isPublished", user1.getObjects()[6])
                .hasFieldOrPropertyWithValue("userId", user1.getObjects()[7])
                .hasFieldOrPropertyWithValue("sourceId", user1.getObjects()[8]);
        soft.assertAll();
        soft.assertThat(resultUserImage1)
                .hasFieldOrPropertyWithValue("id", userImage1.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", userImage1.getObjects()[1])
                .hasFieldOrPropertyWithValue("path", userImage1.getObjects()[2]);
        soft.assertAll();
        soft.assertThat(resultTag1)
                .hasFieldOrPropertyWithValue("id", tag1.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", tag1.getObjects()[1]);
        soft.assertAll();
    }*/

    /*@DisplayName("Обновление сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/user.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/user.sql")
    void updateUser() {
        SoftAssertions soft = new SoftAssertions();
        // статьи
        User user1 = new User(1, "Заголовок 10", "Лид 10", createDateUser,
                editDateUser, "Текст 10", false, 1, 1, 1);
        // изображения
        UserImage userImage1 = new UserImage("Изображение 10", "/static/images/image10.png");
        UserImage userImage2 = new UserImage(1, "Изображение 11", "/static/images/image11.png");
        user1.addNewImage(userImage1);
        user1.addNewImage(userImage2);
        userImage1.setUser(user1);
        userImage2.setUser(user1);
        // тэги
        Tag tag1 = new Tag("Тег 10");
        Tag tag2 = new Tag(1, "Тег 11");
        user1.addNewTag(tag1);
        user1.addNewTag(tag2);
        tag1.addNewUser(user1);
        tag2.addNewUser(user1);

        User result = userRepository.save(user1);
        // получаем изображения
        UserImage resultUserImage1 = (UserImage) result.getImages().toArray()[0];
        UserImage resultUserImage2 = (UserImage) result.getImages().toArray()[1];
        // получаем теги
        Tag resultTag1 = (Tag) result.getTags().toArray()[0];
        Tag resultTag2 = (Tag) result.getTags().toArray()[1];

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("id", user1.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", user1.getObjects()[1])
                .hasFieldOrPropertyWithValue("lead", user1.getObjects()[2])
                .hasFieldOrPropertyWithValue("createDate", user1.getObjects()[3])
                .hasFieldOrPropertyWithValue("editDate", user1.getObjects()[4])
                .hasFieldOrPropertyWithValue("text", user1.getObjects()[5])
                .hasFieldOrPropertyWithValue("isPublished", user1.getObjects()[6])
                .hasFieldOrPropertyWithValue("userId", user1.getObjects()[7])
                .hasFieldOrPropertyWithValue("sourceId", user1.getObjects()[8]);
        soft.assertAll();
        soft.assertThat(resultUserImage1)
                .hasFieldOrPropertyWithValue("id", 3)
                .hasFieldOrPropertyWithValue("title", userImage1.getObjects()[1])
                .hasFieldOrPropertyWithValue("path", userImage1.getObjects()[2]);
        soft.assertAll();
        soft.assertThat(resultUserImage2)
                .hasFieldOrPropertyWithValue("id", userImage2.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", userImage2.getObjects()[1])
                .hasFieldOrPropertyWithValue("path", userImage2.getObjects()[2]);
        soft.assertAll();
        soft.assertThat(resultTag1)
                .hasFieldOrPropertyWithValue("id", 3)
                .hasFieldOrPropertyWithValue("title", tag1.getObjects()[1]);
        soft.assertAll();
        soft.assertThat(resultTag2)
                .hasFieldOrPropertyWithValue("id", tag2.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", tag2.getObjects()[1]);
        soft.assertAll();
    }*/

    /*@DisplayName("Удаление сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/user.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/user.sql")
    void deleteUser() {

        userRepository.deleteById(1);

        assertThat(userRepository.existsById(1)).as("Запись типа User не была удалена").isFalse();
    }*/
}
