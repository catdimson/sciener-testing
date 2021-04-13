package news.dao.repositories;

import news.dao.connection.DBPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;

class CommentRepositoryTest {
    private PostgreSQLContainer container;
    private DBPool poolConnection;
    private static LocalDate lastLogin;
    private static LocalDate dateJoined;
    private static LocalDate createDateComment;
    private static LocalDate editDateComment;
    private static LocalDate createDateArticle;
    private static LocalDate editDateArticle;
    private static LocalDate date;
    private static int articleUserId;
    private static int commentUserId;

    @BeforeAll
    static void beforeAll() {
        date = LocalDate.of(2020, 5, 20);
        // user (дата входа, дара регистрации)
        lastLogin = LocalDate.of(2020, 5, 20);
        dateJoined = LocalDate.of(2019, 5, 20);
        // article (дата создания, дата редактирования, id юзера создавший новость)
        createDateArticle = LocalDate.of(2019, 6, 25);
        editDateArticle = LocalDate.of(2019, 6, 25);
        articleUserId = 1;
        // comment (дата создания, дата редактировани)
        createDateComment = LocalDate.of(2019, 5, 20);
        editDateComment = LocalDate.of(2020, 5, 20);
        commentUserId = 1;
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

        // осздание группы
        String sqlCreateTableGroup = "CREATE TABLE IF NOT EXISTS \"group\" (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "title character varying(40) NOT NULL," +
                "CONSTRAINT group_pk PRIMARY KEY (id)," +
                "CONSTRAINT title_unique_group UNIQUE (title)" +
                ");";
        statement.executeUpdate(sqlCreateTableGroup);
        String sqlInsertInstanceTableGroup = "INSERT INTO \"group\"(title)" +
                "SELECT" +
                "(array['admin', 'editor', 'seo', 'guest'])[iter]" +
                "FROM generate_series(1, 4) as iter;";
        statement.executeUpdate(sqlInsertInstanceTableGroup);

        // создание юзера
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

        // создание источника
        String sqlCreateTableSource = "CREATE TABLE IF NOT EXISTS source (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "title character varying(50) NOT NULL," +
                "url character varying(500) NOT NULL," +
                "CONSTRAINT source_pk PRIMARY KEY (id)" +
                ");";
        statement.executeUpdate(sqlCreateTableSource);
        String sqlCreateSource = "INSERT INTO source (title, url) VALUES('Яндекс ДЗЕН', 'https://zen.yandex.ru/');";
        statement.executeUpdate(sqlCreateSource);

        // создание категории
        String sqlCreateTableCategory = "CREATE TABLE IF NOT EXISTS category (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ), " +
                "title character varying(50) NOT NULL, " +
                "CONSTRAINT category_pk PRIMARY KEY (id)," +
                "CONSTRAINT title_unique_category UNIQUE (title));";
        statement.executeUpdate(sqlCreateTableCategory);
        String sqlCreateCategory = "INSERT INTO category (title) VALUES('Спорт');";
        statement.executeUpdate(sqlCreateCategory);

        // создание article
        String sqlCreateTableArticle = "CREATE TABLE IF NOT EXISTS article (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "title character varying(250) NOT NULL," +
                "lead character varying(350) NOT NULL," +
                "create_date timestamp NOT NULL," +
                "edit_date timestamp NOT NULL," +
                "text text NOT NULL," +
                "is_published boolean DEFAULT false," +
                "category_id integer NOT NULL DEFAULT 1," +
                "user_id integer NOT NULL," +
                "source_id integer," +
                "CONSTRAINT article_pk PRIMARY KEY (id)," +
                "CONSTRAINT fk_category FOREIGN KEY (category_id)" +
                "    REFERENCES category (id) MATCH SIMPLE" +
                "    ON UPDATE CASCADE" +
                "    ON DELETE RESTRICT," +
                "CONSTRAINT fk_user FOREIGN KEY (user_id)" +
                "    REFERENCES \"user\" (id) MATCH SIMPLE" +
                "    ON UPDATE CASCADE" +
                "    ON DELETE RESTRICT," +
                "CONSTRAINT fk_source FOREIGN KEY (source_id)" +
                "    REFERENCES source (id) MATCH SIMPLE" +
                "    ON UPDATE CASCADE" +
                "    ON DELETE RESTRICT);" +
                "CREATE INDEX IF NOT EXISTS fk_index_category_id ON article (category_id);" +
                "CREATE INDEX IF NOT EXISTS fk_index_article_user_id ON article (user_id);" +
                "CREATE INDEX IF NOT EXISTS fk_index_source_id ON article (source_id);";
        statement.executeUpdate(sqlCreateTableArticle);
        String sqlCreateArticle = String.format("INSERT INTO article (title, lead, create_date, edit_date, text, is_published, category_id, user_id, source_id) " +
                "VALUES('title1', 'lead1', '%s', '%s', 'text1', true, 1, %s, 1);", Timestamp.valueOf(createDateArticle.atStartOfDay()),
                Timestamp.valueOf(editDateArticle.atStartOfDay()), articleUserId);
        statement.executeUpdate(sqlCreateArticle);

        // СОЗДАНИЕ ТАБЛИЦЫ КОММЕНТАРИЕВ
        String sqlCreateTableComment = "CREATE TABLE IF NOT EXISTS comment (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),\n" +
                "text character varying(3000)," +
                "create_date timestamp NOT NULL," +
                "edit_date timestamp NOT NULL," +
                "article_id integer NOT NULL," +
                "user_id integer NOT NULL," +
                "CONSTRAINT comment_pk PRIMARY KEY (id)," +
                "CONSTRAINT fk_new FOREIGN KEY (article_id)" +
                "    REFERENCES article (id) MATCH SIMPLE" +
                "    ON UPDATE CASCADE" +
                "    ON DELETE CASCADE," +
                "CONSTRAINT fk_user FOREIGN KEY (user_id)" +
                "    REFERENCES \"user\" (id) MATCH SIMPLE" +
                "    ON UPDATE CASCADE" +
                "    ON DELETE CASCADE);" +
                "CREATE INDEX IF NOT EXISTS fk_index_comment_new_id ON comment (article_id);" +
                "CREATE INDEX IF NOT EXISTS fk_index_comment_user_id ON comment (user_id);";
        statement.executeUpdate(sqlCreateTableComment);

        // СОЗДАНИЕ ТАБЛИЦЫ ПРИКРЕПЛЕНИЙ К КОММЕНТАРИЯМ
        String sqlCreateTableAttachment = "CREATE TABLE IF NOT EXISTS attachment (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "title character varying(80) NOT NULL," +
                "path character varying(500) NOT NULL," +
                "comment_id integer NOT NULL," +
                "CONSTRAINT attachment_pk PRIMARY KEY (id)," +
                "CONSTRAINT fk_comment FOREIGN KEY (comment_id)" +
                "    REFERENCES comment (id) MATCH SIMPLE" +
                "    ON UPDATE CASCADE" +
                "    ON DELETE CASCADE);" +
                "CREATE INDEX IF NOT EXISTS fk_index_attachment_comment_id ON attachment (comment_id);";
        statement.executeUpdate(sqlCreateTableAttachment);
    }

    @Test
    void findById() {
        System.out.println("ТАБЛИЦЫ СОЗДАНЫ");
        /*try {
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
        }*/
    }

    /*@Test
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
            AfishaRepository afishaRepository = new AfishaRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlCreateAfisha = String.format("INSERT INTO afisha" +
                            "(title, image_url, lead, description, age_limit, timing, place, phone, date, is_commercial, user_id, source_id) " +
                            "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', %s, %d, %d);",
                    "Масленица", "/media/maslenica.jpg", "Празничные гуляния на площади", "Описание масленичных гуляний", "0", "180", "Центральная площадь, г.Белгород",
                    "89202005544", Timestamp.valueOf(date.atStartOfDay()), false, 1, 1);
            statement.executeUpdate(sqlCreateAfisha, Statement.RETURN_GENERATED_KEYS);
            Afisha afisha2 = new Afisha(1, "Масленица. Конкурсы.", "/media/maslenicaprazdnik.jpg", "Конкурсы на празник", "Красивое описание масленичных конкурсов",
                    "3", "120", "Кинотеатр русич", "89208880022", date, false, 1, 1);

            afishaRepository.update(afisha2);

            String sqlQueryInstance = String.format("SELECT * FROM afisha WHERE id=%d;", 1);
            ResultSet result = statement.executeQuery(sqlQueryInstance);
            result.next();
            soft.assertThat(afisha2)
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
    }*/
}