package news.dao.repositories;

import news.dao.connection.DBPool;
import news.dao.specifications.FindByIdAfishaSpecification;
import news.dao.specifications.FindByTitleAfishaSpecification;
import news.model.Afisha;
import news.model.User;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AfishaRepositoryTest {
    private PostgreSQLContainer container;
    private DBPool poolConnection;
    private static LocalDate lastLogin;
    private static LocalDate dateJoined;
    private static LocalDate date;
    private static int userId;

    @BeforeAll
    static void beforeAll() {
        date = LocalDate.of(2020, 5, 20);
        lastLogin = LocalDate.of(2020, 5, 20);
        dateJoined = LocalDate.of(2019, 5, 20);
        userId = 1;
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
        String sqlCreateUser = String.format("INSERT INTO \"user\"" +
                        "(password, username, first_name, last_name, email, last_login, date_joined, is_superuser, is_staff, is_active, group_id) " +
                        "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);", "qwerty123", "alex", "Александр", "Колесников", "alex1993@mail.ru",
                Timestamp.valueOf(lastLogin.atStartOfDay()), Timestamp.valueOf(dateJoined.atStartOfDay()), false, true, true, 2);
        statement.executeUpdate(sqlCreateUser);

        String sqlCreateTableSource = "CREATE TABLE IF NOT EXISTS source (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "title character varying(50) NOT NULL," +
                "url character varying(500) NOT NULL," +
                "CONSTRAINT source_pk PRIMARY KEY (id)" +
                ");";
        statement.executeUpdate(sqlCreateTableSource);
        String sqlCreateSource = "INSERT INTO source (title, url) VALUES('Яндекс ДЗЕН', 'https://zen.yandex.ru/');";
        statement.executeUpdate(sqlCreateSource);

        String sqlCreateTableAfisha = "CREATE TABLE IF NOT EXISTS afisha (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "title character varying(250) NOT NULL," +
                "image_url character varying(500)," +
                "lead character varying(350) NOT NULL," +
                "description text NOT NULL," +
                "age_limit character varying(5)," +
                "timing character varying(15)," +
                "place character varying(300)," +
                "phone character varying(20)," +
                "date timestamp," +
                "is_commercial boolean NOT NULL DEFAULT false," +
                "user_id integer NOT NULL," +
                "source_id integer," +
                "CONSTRAINT afisha_pk PRIMARY KEY (id)," +
                "CONSTRAINT fk_source FOREIGN KEY (source_id)" +
                "    REFERENCES source (id) MATCH SIMPLE" +
                "    ON UPDATE CASCADE" +
                "    ON DELETE RESTRICT," +
                "CONSTRAINT fk_user FOREIGN KEY (user_id)" +
                "    REFERENCES \"user\" (id) MATCH SIMPLE" +
                "    ON UPDATE CASCADE" +
                "    ON DELETE RESTRICT" +
                ");" +
                "CREATE INDEX IF NOT EXISTS fk_index_source_id ON afisha (source_id);" +
                "CREATE INDEX IF NOT EXISTS fk_index_source_user_id ON afisha (user_id);";
        statement.executeUpdate(sqlCreateTableAfisha);
    }

    @Test
    void findById() {
        try {
            SoftAssertions soft = new SoftAssertions();
            AfishaRepository afishaRepository = new AfishaRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();

            Afisha afisha = new Afisha(1, "Масленица", "/media/maslenica.jpg", "Празничные гуляния на площади", "Описание масленичных гуляний",
                    "0", "180", "Центральная площадь, г.Белгород", "89202005544", date, false, 1, 1);
            Object[] afishaInstance = afisha.getObjects();

            String sqlCreateInstance = "INSERT INTO afisha" +
                    "(title, image_url, lead, description, age_limit, timing, place, phone, date, is_commercial, user_id, source_id) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement statement = connection.prepareStatement(sqlCreateInstance);
            statement.setString(1, (String) afishaInstance[1]);
            statement.setString(2, (String) afishaInstance[2]);
            statement.setString(3, (String) afishaInstance[3]);
            statement.setString(4, (String) afishaInstance[4]);
            statement.setString(5, (String) afishaInstance[5]);
            statement.setString(6, (String) afishaInstance[6]);
            statement.setString(7, (String) afishaInstance[7]);
            statement.setString(8, (String) afishaInstance[8]);
            statement.setTimestamp(9, Timestamp.valueOf(date.atStartOfDay()));
            statement.setBoolean(10, (boolean) afishaInstance[10]);
            statement.setInt(11, (int) afishaInstance[11]);
            statement.setInt(12, (int) afishaInstance[12]);
            statement.executeUpdate();

            FindByIdAfishaSpecification findById = new FindByIdAfishaSpecification(1);
            List<Afisha> resultFindByIdAfishaList = afishaRepository.query(findById);
            Object[] resultFindByIdAfishaInstance = resultFindByIdAfishaList.get(0).getObjects();

            soft.assertThat(afisha)
                    .hasFieldOrPropertyWithValue("title", resultFindByIdAfishaInstance[1])
                    .hasFieldOrPropertyWithValue("imageUrl", resultFindByIdAfishaInstance[2])
                    .hasFieldOrPropertyWithValue("lead", resultFindByIdAfishaInstance[3])
                    .hasFieldOrPropertyWithValue("description", resultFindByIdAfishaInstance[4])
                    .hasFieldOrPropertyWithValue("ageLimit", resultFindByIdAfishaInstance[5])
                    .hasFieldOrPropertyWithValue("timing", resultFindByIdAfishaInstance[6])
                    .hasFieldOrPropertyWithValue("place", resultFindByIdAfishaInstance[7])
                    .hasFieldOrPropertyWithValue("phone", resultFindByIdAfishaInstance[8])
                    .hasFieldOrPropertyWithValue("date", resultFindByIdAfishaInstance[9])
                    .hasFieldOrPropertyWithValue("isCommercial", resultFindByIdAfishaInstance[10])
                    .hasFieldOrPropertyWithValue("userId", resultFindByIdAfishaInstance[11])
                    .hasFieldOrPropertyWithValue("sourceId", resultFindByIdAfishaInstance[12]);
            soft.assertAll();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void findByTitle() {
        try {
            SoftAssertions soft = new SoftAssertions();
            AfishaRepository afishaRepository = new AfishaRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Afisha afisha = new Afisha(1, "Масленица", "/media/maslenica.jpg", "Празничные гуляния на площади", "Описание масленичных гуляний",
                    "0", "180", "Центральная площадь, г.Белгород", "89202005544", date, false, 1, 1);
            Afisha afisha2 = new Afisha(2, "Масленица", "/media/konkursi.jpg", "Конкурсы", "Описание масленичных конкурсов",
                    "0", "180", "Центральная площадь, г.Белгород", "89202005544", date, false, 1, 1);
            Object[] afishaInstance = afisha.getObjects();
            Object[] afishaInstance2 = afisha2.getObjects();

            String sqlCreateInstance = "INSERT INTO afisha" +
                    "(title, image_url, lead, description, age_limit, timing, place, phone, date, is_commercial, user_id, source_id) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement statement = connection.prepareStatement(sqlCreateInstance);
            statement.setString(1, (String) afishaInstance[1]);
            statement.setString(2, (String) afishaInstance[2]);
            statement.setString(3, (String) afishaInstance[3]);
            statement.setString(4, (String) afishaInstance[4]);
            statement.setString(5, (String) afishaInstance[5]);
            statement.setString(6, (String) afishaInstance[6]);
            statement.setString(7, (String) afishaInstance[7]);
            statement.setString(8, (String) afishaInstance[8]);
            statement.setTimestamp(9, Timestamp.valueOf(date.atStartOfDay()));
            statement.setBoolean(10, (boolean) afishaInstance[10]);
            statement.setInt(11, (int) afishaInstance[11]);
            statement.setInt(12, (int) afishaInstance[12]);
            statement.executeUpdate();

            statement.setString(1, (String) afishaInstance2[1]);
            statement.setString(2, (String) afishaInstance2[2]);
            statement.setString(3, (String) afishaInstance2[3]);
            statement.setString(4, (String) afishaInstance2[4]);
            statement.setString(5, (String) afishaInstance2[5]);
            statement.setString(6, (String) afishaInstance2[6]);
            statement.setString(7, (String) afishaInstance2[7]);
            statement.setString(8, (String) afishaInstance2[8]);
            statement.setTimestamp(9, Timestamp.valueOf(date.atStartOfDay()));
            statement.setBoolean(10, (boolean) afishaInstance2[10]);
            statement.setInt(11, (int) afishaInstance2[11]);
            statement.setInt(12, (int) afishaInstance2[12]);
            statement.executeUpdate();

            FindByTitleAfishaSpecification findByTitle = new FindByTitleAfishaSpecification("Масленица");
            List<Afisha> resultFindByTitleAfishaList = afishaRepository.query(findByTitle);
            Object[] resultFindByTitleAfishaInstance = resultFindByTitleAfishaList.get(0).getObjects();
            Object[] resultFindByTitleAfishaInstance2 = resultFindByTitleAfishaList.get(1).getObjects();

            soft.assertThat(afisha)
                    .hasFieldOrPropertyWithValue("title", resultFindByTitleAfishaInstance[1])
                    .hasFieldOrPropertyWithValue("imageUrl", resultFindByTitleAfishaInstance[2])
                    .hasFieldOrPropertyWithValue("lead", resultFindByTitleAfishaInstance[3])
                    .hasFieldOrPropertyWithValue("description", resultFindByTitleAfishaInstance[4])
                    .hasFieldOrPropertyWithValue("ageLimit", resultFindByTitleAfishaInstance[5])
                    .hasFieldOrPropertyWithValue("timing", resultFindByTitleAfishaInstance[6])
                    .hasFieldOrPropertyWithValue("place", resultFindByTitleAfishaInstance[7])
                    .hasFieldOrPropertyWithValue("phone", resultFindByTitleAfishaInstance[8])
                    .hasFieldOrPropertyWithValue("date", resultFindByTitleAfishaInstance[9])
                    .hasFieldOrPropertyWithValue("isCommercial", resultFindByTitleAfishaInstance[10])
                    .hasFieldOrPropertyWithValue("userId", resultFindByTitleAfishaInstance[11])
                    .hasFieldOrPropertyWithValue("sourceId", resultFindByTitleAfishaInstance[12]);
            soft.assertAll();
            soft.assertThat(afisha2)
                    .hasFieldOrPropertyWithValue("title", resultFindByTitleAfishaInstance2[1])
                    .hasFieldOrPropertyWithValue("imageUrl", resultFindByTitleAfishaInstance2[2])
                    .hasFieldOrPropertyWithValue("lead", resultFindByTitleAfishaInstance2[3])
                    .hasFieldOrPropertyWithValue("description", resultFindByTitleAfishaInstance2[4])
                    .hasFieldOrPropertyWithValue("ageLimit", resultFindByTitleAfishaInstance2[5])
                    .hasFieldOrPropertyWithValue("timing", resultFindByTitleAfishaInstance2[6])
                    .hasFieldOrPropertyWithValue("place", resultFindByTitleAfishaInstance2[7])
                    .hasFieldOrPropertyWithValue("phone", resultFindByTitleAfishaInstance2[8])
                    .hasFieldOrPropertyWithValue("date", resultFindByTitleAfishaInstance2[9])
                    .hasFieldOrPropertyWithValue("isCommercial", resultFindByTitleAfishaInstance2[10])
                    .hasFieldOrPropertyWithValue("userId", resultFindByTitleAfishaInstance2[11])
                    .hasFieldOrPropertyWithValue("sourceId", resultFindByTitleAfishaInstance2[12]);
            soft.assertAll();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void createAfisha() {
        try {
            SoftAssertions soft = new SoftAssertions();
            AfishaRepository afishaRepository = new AfishaRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            Afisha afisha = new Afisha("Масленица", "/media/maslenica.jpg", "Празничные гуляния на площади", "Описание масленичных гуляний",
                    "0", "180", "Центральная площадь, г.Белгород", "89202005544", date, false, 1, 1);

            afishaRepository.create(afisha);

            String sqlQueryInstance = String.format("SELECT * FROM afisha WHERE id=%d;", 1);
            ResultSet result = statement.executeQuery(sqlQueryInstance);
            result.next();
            soft.assertThat(afisha)
                    .hasFieldOrPropertyWithValue("title", result.getString(2))
                    .hasFieldOrPropertyWithValue("imageUrl", result.getString(3))
                    .hasFieldOrPropertyWithValue("lead", result.getString(4))
                    .hasFieldOrPropertyWithValue("description", result.getString(5))
                    .hasFieldOrPropertyWithValue("ageLimit", result.getString(6))
                    .hasFieldOrPropertyWithValue("timing", result.getString(7))
                    .hasFieldOrPropertyWithValue("place", result.getString(8))
                    .hasFieldOrPropertyWithValue("phone", result.getString(9))
                    .hasFieldOrPropertyWithValue("date", result.getTimestamp(10).toLocalDateTime().toLocalDate())
                    .hasFieldOrPropertyWithValue("isCommercial", result.getBoolean(11))
                    .hasFieldOrPropertyWithValue("userId", result.getInt(12))
                    .hasFieldOrPropertyWithValue("sourceId", result.getInt(13));
            soft.assertAll();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void deleteAfisha() {
        try {
            AfishaRepository afishaRepository = new AfishaRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlCreateAfisha = String.format("INSERT INTO afisha" +
                            "(title, image_url, lead, description, age_limit, timing, place, phone, date, is_commercial, user_id, source_id) " +
                            "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', %s, %d, %d);",
                    "Масленица", "/media/maslenica.jpg", "Празничные гуляния на площади", "Описание масленичных гуляний", "0", "180", "Центральная площадь, г.Белгород",
                    "89202005544", Timestamp.valueOf(date.atStartOfDay()), false, 1, 1);
            statement.executeUpdate(sqlCreateAfisha, Statement.RETURN_GENERATED_KEYS);
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();

            afishaRepository.delete(generatedKeys.getInt(1));

            String sqlQueryInstance = String.format("SELECT * FROM afisha WHERE id=%d;", generatedKeys.getInt(1));
            ResultSet result = statement.executeQuery(sqlQueryInstance);
            assertThat(result.next()).as("Запись класса Afisha не была удалена").isFalse();
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