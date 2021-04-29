package news.dao.repositories;

import news.dao.connection.DBPool;
import news.dao.specifications.FindAllUserSpecification;
import news.dao.specifications.FindByFirstnameUserSpecification;
import news.dao.specifications.FindByIdUserSpecification;
import news.model.User;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest {
    private PostgreSQLContainer container;
    private DBPool poolConnection;
    private static LocalDate lastLogin;
    private static LocalDate dateJoined;
    private static int groupId;

    @BeforeAll
    static void beforeAll() {
        lastLogin = LocalDate.of(2020, 5, 20);
        dateJoined = LocalDate.of(2019, 5, 20);
        groupId = 1;
    }

    @BeforeEach
    void setUp() throws SQLException {
        this.container = new PostgreSQLContainer("postgres")
                .withUsername("admin")
                .withPassword("qwerty")
                .withDatabaseName("news");
        this.container.start();
        this.poolConnection = new DBPool(this.container.getJdbcUrl(), this.container.getUsername(), this.container.getPassword());
        Statement statement = this.poolConnection.getConnection().createStatement();

        String sqlCreateTableGroup = "CREATE TABLE IF NOT EXISTS \"group\" (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "title character varying(40) NOT NULL," +
                "CONSTRAINT group_pk PRIMARY KEY (id)," +
                "CONSTRAINT title_unique UNIQUE (title)" +
                ");";
        statement.executeUpdate(sqlCreateTableGroup);

        String sqlInsertInstanceTableGroup = "INSERT INTO \"group\"(title)" +
                "SELECT" +
                "(array['admin', 'editor', 'seo', 'guest'])[iter]" +
                "FROM generate_series(1, 4) as iter;";
        statement.executeUpdate(sqlInsertInstanceTableGroup);

        String sqlCreateTableUser = "CREATE TABLE IF NOT EXISTS \"user\"  (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "password character varying(128) NOT NULL," +
                "username character varying(150) NOT NULL," +
                "first_name character varying(150) NOT NULL," +
                "last_name character varying(150)," +
                "email character varying(254) NOT NULL," +
                "last_login timestamp NOT NULL," +
                "date_joined timestamp NOT NULL," +
                "is_superuser boolean NOT NULL DEFAULT false," +
                "is_staff boolean NOT NULL DEFAULT false," +
                "is_active boolean NOT NULL DEFAULT true," +
                "group_id integer NOT NULL," +
                "CONSTRAINT user_pk PRIMARY KEY (id)," +
                "CONSTRAINT username_unique UNIQUE (username)," +
                "CONSTRAINT fk_user_group_id FOREIGN KEY (group_id)" +
                "    REFERENCES \"group\" (id) MATCH SIMPLE" +
                "    ON UPDATE CASCADE" +
                "    ON DELETE RESTRICT" +
                ");";
        statement.executeUpdate(sqlCreateTableUser);
    }

    @Test
    void findById() {
        try {
            SoftAssertions soft = new SoftAssertions();
            UserRepository userRepository = new UserRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();

            User user = new User(1, "qwerty123", "alex1992", "Александр", "Колесников", "alex1993@mail.ru", lastLogin, dateJoined,
                    true, true, true, 1);
            Object[] userInstance = user.getObjects();

            String sqlCreateInstance = "INSERT INTO \"user\"" +
                    "(password, username, first_name, last_name, email, last_login, date_joined, is_superuser, is_staff, is_active, group_id) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement statement = connection.prepareStatement(sqlCreateInstance);
            statement.setString(1, (String) userInstance[1]);
            statement.setString(2, (String) userInstance[2]);
            statement.setString(3, (String) userInstance[3]);
            statement.setString(4, (String) userInstance[4]);
            statement.setString(5, (String) userInstance[5]);
            LocalDate localDateLogin = (LocalDate) userInstance[6];
            statement.setTimestamp(6, Timestamp.valueOf(localDateLogin.atStartOfDay()));
            LocalDate localDateJoined = (LocalDate) userInstance[7];
            statement.setTimestamp(7, Timestamp.valueOf(localDateJoined.atStartOfDay()));
            statement.setBoolean(8, (boolean) userInstance[8]);
            statement.setBoolean(9, (boolean) userInstance[9]);
            statement.setBoolean(10, (boolean) userInstance[10]);
            statement.setInt(11, (int) userInstance[11]);
            statement.executeUpdate();

            FindByIdUserSpecification findById = new FindByIdUserSpecification(1);
            List<User> resultFindByIdUserList = userRepository.query(findById);
            Object[] resultFindByIdUserInstance = resultFindByIdUserList.get(0).getObjects();

            soft.assertThat(user)
                    .hasFieldOrPropertyWithValue("id", resultFindByIdUserInstance[0])
                    .hasFieldOrPropertyWithValue("password", resultFindByIdUserInstance[1])
                    .hasFieldOrPropertyWithValue("username", resultFindByIdUserInstance[2])
                    .hasFieldOrPropertyWithValue("firstName", resultFindByIdUserInstance[3])
                    .hasFieldOrPropertyWithValue("lastName", resultFindByIdUserInstance[4])
                    .hasFieldOrPropertyWithValue("email", resultFindByIdUserInstance[5])
                    .hasFieldOrPropertyWithValue("lastLogin", resultFindByIdUserInstance[6])
                    .hasFieldOrPropertyWithValue("dateJoined", resultFindByIdUserInstance[7])
                    .hasFieldOrPropertyWithValue("isSuperuser", resultFindByIdUserInstance[8])
                    .hasFieldOrPropertyWithValue("isStaff", resultFindByIdUserInstance[9])
                    .hasFieldOrPropertyWithValue("isActive", resultFindByIdUserInstance[10])
                    .hasFieldOrPropertyWithValue("groupId", resultFindByIdUserInstance[11]);
            soft.assertAll();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void findByFirstname() {
        try {
            SoftAssertions soft = new SoftAssertions();
            UserRepository userRepository = new UserRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();

            User user = new User(1, "qwerty123", "alex1992", "Александр", "Колесников", "alex1993@mail.ru", lastLogin, dateJoined,
                    true, true, true, 1);
            User user2 = new User(4, "ytrewq321", "cyber777", "Александр", "Жбанов", "jban1990@mail.ru", lastLogin, dateJoined,
                    false, false, true, 2);
            Object[] userInstance = user.getObjects();
            Object[] userInstance2 = user2.getObjects();
            LocalDate localDateLogin = (LocalDate) userInstance[6];
            LocalDate localDateJoined = (LocalDate) userInstance[7];
            LocalDate localDateLogin2 = (LocalDate) userInstance2[6];
            LocalDate localDateJoined2 = (LocalDate) userInstance2[7];

            String sqlCreateUser1 = String.format("INSERT INTO \"user\"" +
                    "(password, username, first_name, last_name, email, last_login, date_joined, is_superuser, is_staff, is_active, group_id) " +
                    "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);", userInstance[1], userInstance[2], userInstance[3], userInstance[4], userInstance[5],
                    Timestamp.valueOf(localDateLogin.atStartOfDay()), Timestamp.valueOf(localDateJoined.atStartOfDay()), userInstance[8], userInstance[9], userInstance[10], userInstance[11]);
            String sqlCreateUser2 = String.format("INSERT INTO \"user\"" +
                    "(password, username, first_name, last_name, email, last_login, date_joined, is_superuser, is_staff, is_active, group_id) " +
                    "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);", userInstance2[1], userInstance2[2], userInstance2[3], userInstance2[4], userInstance2[5],
                    Timestamp.valueOf(localDateLogin2.atStartOfDay()), Timestamp.valueOf(localDateJoined2.atStartOfDay()), userInstance2[8], userInstance2[9], userInstance2[10], userInstance2[11]);

            Statement statement = connection.createStatement();
            statement.executeUpdate(sqlCreateUser1);
            statement.executeUpdate(sqlCreateUser2);

            FindByFirstnameUserSpecification findByFirstname = new FindByFirstnameUserSpecification("Александр");
            List<User> resultFindByFirstnameUserList = userRepository.query(findByFirstname);
            Object[] resultFindByFirstnameUserInstance = resultFindByFirstnameUserList.get(0).getObjects();
            Object[] resultFindByFirstnameUserInstance2 = resultFindByFirstnameUserList.get(1).getObjects();

            soft.assertThat(user)
                    .hasFieldOrPropertyWithValue("password", resultFindByFirstnameUserInstance[1])
                    .hasFieldOrPropertyWithValue("username", resultFindByFirstnameUserInstance[2])
                    .hasFieldOrPropertyWithValue("firstName", resultFindByFirstnameUserInstance[3])
                    .hasFieldOrPropertyWithValue("lastName", resultFindByFirstnameUserInstance[4])
                    .hasFieldOrPropertyWithValue("email", resultFindByFirstnameUserInstance[5])
                    .hasFieldOrPropertyWithValue("lastLogin", resultFindByFirstnameUserInstance[6])
                    .hasFieldOrPropertyWithValue("dateJoined", resultFindByFirstnameUserInstance[7])
                    .hasFieldOrPropertyWithValue("isSuperuser", resultFindByFirstnameUserInstance[8])
                    .hasFieldOrPropertyWithValue("isStaff", resultFindByFirstnameUserInstance[9])
                    .hasFieldOrPropertyWithValue("isActive", resultFindByFirstnameUserInstance[10])
                    .hasFieldOrPropertyWithValue("groupId", resultFindByFirstnameUserInstance[11]);
            soft.assertAll();
            soft.assertThat(user2)
                    .hasFieldOrPropertyWithValue("password", resultFindByFirstnameUserInstance2[1])
                    .hasFieldOrPropertyWithValue("username", resultFindByFirstnameUserInstance2[2])
                    .hasFieldOrPropertyWithValue("firstName", resultFindByFirstnameUserInstance2[3])
                    .hasFieldOrPropertyWithValue("lastName", resultFindByFirstnameUserInstance2[4])
                    .hasFieldOrPropertyWithValue("email", resultFindByFirstnameUserInstance2[5])
                    .hasFieldOrPropertyWithValue("lastLogin", resultFindByFirstnameUserInstance2[6])
                    .hasFieldOrPropertyWithValue("dateJoined", resultFindByFirstnameUserInstance2[7])
                    .hasFieldOrPropertyWithValue("isSuperuser", resultFindByFirstnameUserInstance2[8])
                    .hasFieldOrPropertyWithValue("isStaff", resultFindByFirstnameUserInstance2[9])
                    .hasFieldOrPropertyWithValue("isActive", resultFindByFirstnameUserInstance2[10])
                    .hasFieldOrPropertyWithValue("groupId", resultFindByFirstnameUserInstance2[11]);
            soft.assertAll();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void findAll() {
        try {
            SoftAssertions soft = new SoftAssertions();
            UserRepository userRepository = new UserRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();

            User user = new User(1, "qwerty123", "alex1992", "Александр", "Колесников", "alex1993@mail.ru", lastLogin, dateJoined,
                    true, true, true, 1);
            User user2 = new User(4, "ytrewq321", "cyber777", "Александр", "Жбанов", "jban1990@mail.ru", lastLogin, dateJoined,
                    false, false, true, 2);
            Object[] userInstance = user.getObjects();
            Object[] userInstance2 = user2.getObjects();
            LocalDate localDateLogin = (LocalDate) userInstance[6];
            LocalDate localDateJoined = (LocalDate) userInstance[7];
            LocalDate localDateLogin2 = (LocalDate) userInstance2[6];
            LocalDate localDateJoined2 = (LocalDate) userInstance2[7];

            String sqlCreateUser1 = String.format("INSERT INTO \"user\"" +
                    "(password, username, first_name, last_name, email, last_login, date_joined, is_superuser, is_staff, is_active, group_id) " +
                    "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);", userInstance[1], userInstance[2], userInstance[3], userInstance[4], userInstance[5],
                    Timestamp.valueOf(localDateLogin.atStartOfDay()), Timestamp.valueOf(localDateJoined.atStartOfDay()), userInstance[8], userInstance[9], userInstance[10], userInstance[11]);
            String sqlCreateUser2 = String.format("INSERT INTO \"user\"" +
                    "(password, username, first_name, last_name, email, last_login, date_joined, is_superuser, is_staff, is_active, group_id) " +
                    "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);", userInstance2[1], userInstance2[2], userInstance2[3], userInstance2[4], userInstance2[5],
                    Timestamp.valueOf(localDateLogin2.atStartOfDay()), Timestamp.valueOf(localDateJoined2.atStartOfDay()), userInstance2[8], userInstance2[9], userInstance2[10], userInstance2[11]);

            Statement statement = connection.createStatement();
            statement.executeUpdate(sqlCreateUser1);
            statement.executeUpdate(sqlCreateUser2);

            FindAllUserSpecification findAll = new FindAllUserSpecification();
            List<User> resultFindAllUserList = userRepository.query(findAll);
            Object[] resultFindAllUserInstance = resultFindAllUserList.get(0).getObjects();
            Object[] resultFindAllUserInstance2 = resultFindAllUserList.get(1).getObjects();

            soft.assertThat(user)
                    .hasFieldOrPropertyWithValue("password", resultFindAllUserInstance[1])
                    .hasFieldOrPropertyWithValue("username", resultFindAllUserInstance[2])
                    .hasFieldOrPropertyWithValue("firstName", resultFindAllUserInstance[3])
                    .hasFieldOrPropertyWithValue("lastName", resultFindAllUserInstance[4])
                    .hasFieldOrPropertyWithValue("email", resultFindAllUserInstance[5])
                    .hasFieldOrPropertyWithValue("lastLogin", resultFindAllUserInstance[6])
                    .hasFieldOrPropertyWithValue("dateJoined", resultFindAllUserInstance[7])
                    .hasFieldOrPropertyWithValue("isSuperuser", resultFindAllUserInstance[8])
                    .hasFieldOrPropertyWithValue("isStaff", resultFindAllUserInstance[9])
                    .hasFieldOrPropertyWithValue("isActive", resultFindAllUserInstance[10])
                    .hasFieldOrPropertyWithValue("groupId", resultFindAllUserInstance[11]);
            soft.assertAll();
            soft.assertThat(user2)
                    .hasFieldOrPropertyWithValue("password", resultFindAllUserInstance2[1])
                    .hasFieldOrPropertyWithValue("username", resultFindAllUserInstance2[2])
                    .hasFieldOrPropertyWithValue("firstName", resultFindAllUserInstance2[3])
                    .hasFieldOrPropertyWithValue("lastName", resultFindAllUserInstance2[4])
                    .hasFieldOrPropertyWithValue("email", resultFindAllUserInstance2[5])
                    .hasFieldOrPropertyWithValue("lastLogin", resultFindAllUserInstance2[6])
                    .hasFieldOrPropertyWithValue("dateJoined", resultFindAllUserInstance2[7])
                    .hasFieldOrPropertyWithValue("isSuperuser", resultFindAllUserInstance2[8])
                    .hasFieldOrPropertyWithValue("isStaff", resultFindAllUserInstance2[9])
                    .hasFieldOrPropertyWithValue("isActive", resultFindAllUserInstance2[10])
                    .hasFieldOrPropertyWithValue("groupId", resultFindAllUserInstance2[11]);
            soft.assertAll();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void createUser() {
        try {
            SoftAssertions soft = new SoftAssertions();
            UserRepository userRepository = new UserRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            User user = new User("qwerty123", "alex1992", "Александр", "Колесников", "alex1993@mail.ru", lastLogin, dateJoined,
                    true, true, true, 1);

            userRepository.create(user);

            String sqlQueryInstance = String.format("SELECT * FROM \"user\" WHERE id=%d;", 1);
            ResultSet result = statement.executeQuery(sqlQueryInstance);
            result.next();
            soft.assertThat(user)
                    .hasFieldOrPropertyWithValue("username", result.getString(3))
                    .hasFieldOrPropertyWithValue("firstName", result.getString(4))
                    .hasFieldOrPropertyWithValue("lastName", result.getString(5))
                    .hasFieldOrPropertyWithValue("email", result.getString(6))
                    .hasFieldOrPropertyWithValue("lastLogin", result.getTimestamp(7).toLocalDateTime().toLocalDate())
                    .hasFieldOrPropertyWithValue("dateJoined", result.getTimestamp(8).toLocalDateTime().toLocalDate())
                    .hasFieldOrPropertyWithValue("isSuperuser", result.getBoolean(9))
                    .hasFieldOrPropertyWithValue("isStaff", result.getBoolean(10))
                    .hasFieldOrPropertyWithValue("isActive", result.getBoolean(11))
                    .hasFieldOrPropertyWithValue("groupId", result.getInt(12));
            soft.assertAll();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void deleteUser() {
        try {
            UserRepository userRepository = new UserRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            User user = new User(1, "qwerty123", "alex1992", "Александр", "Колесников", "alex1993@mail.ru", lastLogin, dateJoined,
                    true, true, true, 1);
            Object[] userInstance = user.getObjects();
            LocalDate localDateLogin = (LocalDate) userInstance[6];
            LocalDate localDateJoined = (LocalDate) userInstance[7];
            String sqlCreateUser = String.format("INSERT INTO \"user\"" +
                            "(password, username, first_name, last_name, email, last_login, date_joined, is_superuser, is_staff, is_active, group_id) " +
                            "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);", userInstance[1], userInstance[2], userInstance[3], userInstance[4], userInstance[5],
                    Timestamp.valueOf(localDateLogin.atStartOfDay()), Timestamp.valueOf(localDateJoined.atStartOfDay()), userInstance[8], userInstance[9], userInstance[10], userInstance[11]);
            statement.executeUpdate(sqlCreateUser, Statement.RETURN_GENERATED_KEYS);
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();

            userRepository.delete(generatedKeys.getInt(1));

            String sqlQueryInstance = String.format("SELECT * FROM \"user\" WHERE id=%d;", generatedKeys.getInt(1));
            ResultSet result = statement.executeQuery(sqlQueryInstance);
            assertThat(result.next()).as("Запись класса User не была удалена").isFalse();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void updateUser() {
        try {
            SoftAssertions soft = new SoftAssertions();
            UserRepository userRepository = new UserRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            User user = new User("qwerty123", "alex1992", "Александр", "Колесников", "alex1993@mail.ru", lastLogin, dateJoined,
                    true, true, true, 1);
            User user2 = new User(1, "ytrewq321", "cyber777", "Александр", "Жбанов", "jban1990@mail.ru", lastLogin, dateJoined,
                    false, false, true, 2);
            Object[] userInstance = user.getObjects();
            LocalDate localDateLogin = (LocalDate) userInstance[6];
            LocalDate localDateJoined = (LocalDate) userInstance[7];
            String sqlCreateUser1 = String.format("INSERT INTO \"user\"" +
                            "(password, username, first_name, last_name, email, last_login, date_joined, is_superuser, is_staff, is_active, group_id) " +
                            "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);", userInstance[1], userInstance[2], userInstance[3], userInstance[4], userInstance[5],
                    Timestamp.valueOf(localDateLogin.atStartOfDay()), Timestamp.valueOf(localDateJoined.atStartOfDay()), userInstance[8], userInstance[9], userInstance[10], userInstance[11]);
            statement.executeUpdate(sqlCreateUser1);

            userRepository.update(user2);

            String sqlQueryInstance = String.format("SELECT * FROM \"user\" WHERE id=%d;", 1);
            ResultSet result = statement.executeQuery(sqlQueryInstance);
            result.next();
            soft.assertThat(user2)
                    .hasFieldOrPropertyWithValue("username", result.getString(3))
                    .hasFieldOrPropertyWithValue("firstName", result.getString(4))
                    .hasFieldOrPropertyWithValue("lastName", result.getString(5))
                    .hasFieldOrPropertyWithValue("email", result.getString(6))
                    .hasFieldOrPropertyWithValue("lastLogin", result.getTimestamp(7).toLocalDateTime().toLocalDate())
                    .hasFieldOrPropertyWithValue("dateJoined", result.getTimestamp(8).toLocalDateTime().toLocalDate())
                    .hasFieldOrPropertyWithValue("isSuperuser", result.getBoolean(9))
                    .hasFieldOrPropertyWithValue("isStaff", result.getBoolean(10))
                    .hasFieldOrPropertyWithValue("isActive", result.getBoolean(11))
                    .hasFieldOrPropertyWithValue("groupId", result.getInt(12));
            soft.assertAll();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}