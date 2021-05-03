package news.web.controllers;

import news.dao.connection.DBPool;
import news.model.Article;
import news.web.http.WebServer;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleControllerTest {
    private PostgreSQLContainer container;
    private DBPool poolConnection;
    // для юзера
    private static LocalDate lastLogin;
    private static LocalDate dateJoined;
    // для статьи
    private static LocalDate createDateArticle;
    private static LocalDate editDateArticle;
    private static LocalDate date;
    private static int articleUserId;
    private static int commentUserId;
    // для клиента
    private static Socket clientSocket;
    private static BufferedReader reader;
    private static BufferedReader in;
    private static PrintWriter out;
    private static WebServer webServer;

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

        // создание группы
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
        sqlCreateUser = String.format("INSERT INTO \"user\"" +
                        "(password, username, first_name, last_name, email, last_login, date_joined, is_superuser, is_staff, is_active, group_id) " +
                        "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);", "qwerty000", "max", "Максим", "Вердилов", "maxiver@mail.ru",
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
        String sqlCreateSource = "INSERT INTO source (title, url) VALUES ('Яндекс ДЗЕН', 'https://zen.yandex.ru/'), ('РИА', 'https://ria.ru/');";
        statement.executeUpdate(sqlCreateSource);

        // создание категории
        String sqlCreateTableCategory = "CREATE TABLE IF NOT EXISTS category (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ), " +
                "title character varying(50) NOT NULL, " +
                "CONSTRAINT category_pk PRIMARY KEY (id)," +
                "CONSTRAINT title_unique_category UNIQUE (title));";
        statement.executeUpdate(sqlCreateTableCategory);
        String sqlCreateCategory = "INSERT INTO category (title) VALUES ('Спорт'), ('Политика'), ('Шляпенция еще какая-то');";
        statement.executeUpdate(sqlCreateCategory);

        // создание тега
        String sqlCreateTableTag = "CREATE TABLE IF NOT EXISTS tag (" +
                "    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "    title character varying(50) NOT NULL," +
                "    CONSTRAINT tag_pk PRIMARY KEY (id)" +
                ");";
        statement.executeUpdate(sqlCreateTableTag);
        String sqlCreateTag = "INSERT INTO tag (title) VALUES ('ufc'), ('смешанные единоборства'), ('макгрегор'), " +
                "('балет'), ('картины');";
        statement.executeUpdate(sqlCreateTag);

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

        // создание изображения
        String sqlCreateTableImage = "" +
                "CREATE TABLE IF NOT EXISTS image (" +
                "    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "    title character varying(80) NOT NULL," +
                "    path character varying(500) NOT NULL," +
                "    article_id integer NOT NULL," +
                "    CONSTRAINT image_pk PRIMARY KEY (id)," +
                "    CONSTRAINT fk_article FOREIGN KEY (article_id)" +
                "        REFERENCES article (id) MATCH SIMPLE" +
                "        ON UPDATE CASCADE" +
                "        ON DELETE CASCADE" +
                ");" +
                "CREATE INDEX IF NOT EXISTS fk_index_image_article_id ON image (article_id);";
        statement.executeUpdate(sqlCreateTableImage);

        // создание таблицы article_tag
        String sqlCreateTableArticleTag = "CREATE TABLE IF NOT EXISTS article_tag (" +
                "    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "    article_id integer NOT NULL," +
                "    tag_id integer NOT NULL," +
                "    CONSTRAINT article_tag_pk PRIMARY KEY (id)," +
                "    CONSTRAINT article_tag_unique UNIQUE (article_id, tag_id)," +
                "    CONSTRAINT fk_new FOREIGN KEY (article_id)" +
                "        REFERENCES article (id) MATCH SIMPLE" +
                "        ON UPDATE CASCADE" +
                "        ON DELETE CASCADE," +
                "    CONSTRAINT fk_tag FOREIGN KEY (tag_id)" +
                "        REFERENCES tag (id) MATCH SIMPLE" +
                "        ON UPDATE CASCADE" +
                "        ON DELETE CASCADE);" +
                "CREATE INDEX IF NOT EXISTS fk_index_new_tag_article_id ON article_tag (article_id);" +
                "CREATE INDEX IF NOT EXISTS fk_index_new_tag_tag_id ON article_tag (tag_id);";
        statement.executeUpdate(sqlCreateTableArticleTag);
    }

    @Test
    void buildResponseGETMethodFindAll() throws IOException, SQLException {
        // Добавляем статьи
        Connection connection = this.poolConnection.getConnection();
        Statement statement = connection.createStatement();
        String sqlInsertArticle1 = String.format("INSERT INTO article (title, lead, create_date, edit_date, text, is_published, " +
                        "category_id, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);",
                "Заголовок 1", "Лид 1", Timestamp.valueOf(createDateArticle.atStartOfDay()),
                Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 1", true, 1, 1, 1);
        statement.executeUpdate(sqlInsertArticle1);
        String sqlInsertArticle2 = String.format("INSERT INTO article (title, lead, create_date, edit_date, text, is_published, " +
                        "category_id, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);",
                "Заголовок 1", "Лид 2", Timestamp.valueOf(createDateArticle.atStartOfDay()),
                Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 2", true, 2, 2, 2);
        statement.executeUpdate(sqlInsertArticle2);
        String sqlInsertImages = String.format("INSERT INTO image (title, path, article_id) " +
                        "VALUES ('%s', '%s', %d), ('%s', '%s', %d), ('%s', '%s', %d), ('%s', '%s', %d);",
                "Изображение 1", "/static/images/image1.png", 1,
                "Изображение 2", "/static/images/image2.png", 1,
                "Изображение 3", "/static/images/image3.png", 2,
                "Изображение 4", "/static/images/image4.png", 2);
        statement.executeUpdate(sqlInsertImages);
        String sqlInsertTagsId = "INSERT INTO article_tag (article_id, tag_id) VALUES " +
                "(1, 1), (1, 2), (2, 3), (2, 4);";
        statement.executeUpdate(sqlInsertTagsId);
        // ожидаемый результат
        String expectedResult = "" +
            "HTTP/1.1 200 OK\n" +
            "Cache-Control: no-store, no-cache, must-revalidate\n" +
            "Pragma: no-cache\n" +
            "Content-Type: application/json; charset=UTF-8\n" +
            "\n" +
            "[\n" +
            "{\n" +
            "\t\"id\":1,\n" +
            "\t\"title\":\"Заголовок 1\",\n" +
            "\t\"lead\":\"Лид 1\",\n" +
            "\t\"createDate\":1561410000,\n" +
            "\t\"editDate\":1561410000,\n" +
            "\t\"text\":\"Текст 1\",\n" +
            "\t\"isPublished\":true,\n" +
            "\t\"categoryId\":1,\n" +
            "\t\"userId\":1,\n" +
            "\t\"sourceId\":1,\n" +
            "\t\"images\":[\n" +
            "\t\t{\n" +
            "\t\t\t\"id\":1,\n" +
            "\t\t\t\"title\":\"Изображение 1\",\n" +
            "\t\t\t\"path\":\"/static/images/image1.png\",\n" +
            "\t\t\t\"articleId\":1\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"id\":2,\n" +
            "\t\t\t\"title\":\"Изображение 2\",\n" +
            "\t\t\t\"path\":\"/static/images/image2.png\",\n" +
            "\t\t\t\"articleId\":1\n" +
            "\t\t}\n" +
            "\t],\n" +
            "\t\"tagsId\":[\n" +
            "\t\t1,\n" +
            "\t\t2\n" +
            "\t]\n" +
            "},\n" +
            "{\n" +
            "\t\"id\":2,\n" +
            "\t\"title\":\"Заголовок 1\",\n" +
            "\t\"lead\":\"Лид 2\",\n" +
            "\t\"createDate\":1561410000,\n" +
            "\t\"editDate\":1561410000,\n" +
            "\t\"text\":\"Текст 2\",\n" +
            "\t\"isPublished\":true,\n" +
            "\t\"categoryId\":2,\n" +
            "\t\"userId\":2,\n" +
            "\t\"sourceId\":2,\n" +
            "\t\"images\":[\n" +
            "\t\t{\n" +
            "\t\t\t\"id\":3,\n" +
            "\t\t\t\"title\":\"Изображение 3\",\n" +
            "\t\t\t\"path\":\"/static/images/image3.png\",\n" +
            "\t\t\t\"articleId\":2\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"id\":4,\n" +
            "\t\t\t\"title\":\"Изображение 4\",\n" +
            "\t\t\t\"path\":\"/static/images/image4.png\",\n" +
            "\t\t\t\"articleId\":2\n" +
            "\t\t}\n" +
            "\t],\n" +
            "\t\"tagsId\":[\n" +
            "\t\t3,\n" +
            "\t\t4\n" +
            "\t]\n" +
            "}\n" +
            "]\n";
        clientSocket = new Socket("127.0.0.1", 5000);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));

        String request = "" +
                "GET /article/ HTTP/1.1\n" +
                "Accept: application/json, */*; q=0.01\n" +
                "Content-Type: application/json\n" +
                "Host: 127.0.0.1:5000\n" +
                "UnitTest: true\n" +
                "UrlPostgres: " + this.container.getJdbcUrl() + "\n" +
                "UserPostgres: " + this.container.getUsername() + "\n" +
                "PasswordPostgres: " + this.container.getPassword() + "\n";
        out.println(request);
        out.flush();

        StringBuilder actualResult = new StringBuilder();
        actualResult.append(in.readLine()).append("\n");
        while (in.ready()) {
            actualResult.append(in.readLine()).append("\n");
        }
        actualResult.setLength(actualResult.length() - 1);
        assertThat(actualResult.toString()).isEqualTo(expectedResult);
   }

    @Test
    void buildResponseGETMethodFindByTitle() throws IOException, SQLException {
        // Добавляем статьи
        Connection connection = this.poolConnection.getConnection();
        Statement statement = connection.createStatement();
        String sqlInsertArticle1 = String.format("INSERT INTO article (title, lead, create_date, edit_date, text, is_published, " +
                        "category_id, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);",
                "Заголовок1", "Лид 1", Timestamp.valueOf(createDateArticle.atStartOfDay()),
                Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 1", true, 1, 1, 1);
        statement.executeUpdate(sqlInsertArticle1);
        String sqlInsertArticle2 = String.format("INSERT INTO article (title, lead, create_date, edit_date, text, is_published, " +
                        "category_id, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);",
                "Заголовок1", "Лид 2", Timestamp.valueOf(createDateArticle.atStartOfDay()),
                Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 2", true, 2, 2, 2);
        statement.executeUpdate(sqlInsertArticle2);
        String sqlInsertImages = String.format("INSERT INTO image (title, path, article_id) " +
                        "VALUES ('%s', '%s', %d), ('%s', '%s', %d), ('%s', '%s', %d), ('%s', '%s', %d);",
                "Изображение 1", "/static/images/image1.png", 1,
                "Изображение 2", "/static/images/image2.png", 1,
                "Изображение 3", "/static/images/image3.png", 2,
                "Изображение 4", "/static/images/image4.png", 2);
        statement.executeUpdate(sqlInsertImages);
        String sqlInsertTagsId = "INSERT INTO article_tag (article_id, tag_id) VALUES " +
                "(1, 1), (1, 2), (2, 3), (2, 4);";
        statement.executeUpdate(sqlInsertTagsId);
        // ожидаемый результат
        String expectedResult = "" +
                "HTTP/1.1 200 OK\n" +
                "Cache-Control: no-store, no-cache, must-revalidate\n" +
                "Pragma: no-cache\n" +
                "Content-Type: application/json; charset=UTF-8\n" +
                "\n" +
                "[\n" +
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"title\":\"Заголовок1\",\n" +
                "\t\"lead\":\"Лид 1\",\n" +
                "\t\"createDate\":1561410000,\n" +
                "\t\"editDate\":1561410000,\n" +
                "\t\"text\":\"Текст 1\",\n" +
                "\t\"isPublished\":true,\n" +
                "\t\"categoryId\":1,\n" +
                "\t\"userId\":1,\n" +
                "\t\"sourceId\":1,\n" +
                "\t\"images\":[\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":1,\n" +
                "\t\t\t\"title\":\"Изображение 1\",\n" +
                "\t\t\t\"path\":\"/static/images/image1.png\",\n" +
                "\t\t\t\"articleId\":1\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":2,\n" +
                "\t\t\t\"title\":\"Изображение 2\",\n" +
                "\t\t\t\"path\":\"/static/images/image2.png\",\n" +
                "\t\t\t\"articleId\":1\n" +
                "\t\t}\n" +
                "\t],\n" +
                "\t\"tagsId\":[\n" +
                "\t\t1,\n" +
                "\t\t2\n" +
                "\t]\n" +
                "},\n" +
                "{\n" +
                "\t\"id\":2,\n" +
                "\t\"title\":\"Заголовок1\",\n" +
                "\t\"lead\":\"Лид 2\",\n" +
                "\t\"createDate\":1561410000,\n" +
                "\t\"editDate\":1561410000,\n" +
                "\t\"text\":\"Текст 2\",\n" +
                "\t\"isPublished\":true,\n" +
                "\t\"categoryId\":2,\n" +
                "\t\"userId\":2,\n" +
                "\t\"sourceId\":2,\n" +
                "\t\"images\":[\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":3,\n" +
                "\t\t\t\"title\":\"Изображение 3\",\n" +
                "\t\t\t\"path\":\"/static/images/image3.png\",\n" +
                "\t\t\t\"articleId\":2\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":4,\n" +
                "\t\t\t\"title\":\"Изображение 4\",\n" +
                "\t\t\t\"path\":\"/static/images/image4.png\",\n" +
                "\t\t\t\"articleId\":2\n" +
                "\t\t}\n" +
                "\t],\n" +
                "\t\"tagsId\":[\n" +
                "\t\t3,\n" +
                "\t\t4\n" +
                "\t]\n" +
                "}\n" +
                "]\n";
        clientSocket = new Socket("127.0.0.1", 5000);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));

        String request = "" +
                "GET /article?title=Заголовок1 HTTP/1.1\n" +
                "Accept: application/json, */*; q=0.01\n" +
                "Content-Type: application/json\n" +
                "Host: 127.0.0.1:5000\n" +
                "UnitTest: true\n" +
                "UrlPostgres: " + this.container.getJdbcUrl() + "\n" +
                "UserPostgres: " + this.container.getUsername() + "\n" +
                "PasswordPostgres: " + this.container.getPassword() + "\n";
        out.println(request);
        out.flush();

        StringBuilder actualResult = new StringBuilder();
        actualResult.append(in.readLine()).append("\n");
        while (in.ready()) {
            actualResult.append(in.readLine()).append("\n");
        }
        actualResult.setLength(actualResult.length() - 1);
        assertThat(actualResult.toString()).isEqualTo(expectedResult);
    }

    @Test
    void buildResponseGETMethodFindById() throws SQLException, IOException {
        // Добавляем статьи
        Connection connection = this.poolConnection.getConnection();
        Statement statement = connection.createStatement();
        String sqlInsertArticle1 = String.format("INSERT INTO article (title, lead, create_date, edit_date, text, is_published, " +
                        "category_id, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);",
                "Заголовок1", "Лид 1", Timestamp.valueOf(createDateArticle.atStartOfDay()),
                Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 1", true, 1, 1, 1);
        statement.executeUpdate(sqlInsertArticle1);
        String sqlInsertArticle2 = String.format("INSERT INTO article (title, lead, create_date, edit_date, text, is_published, " +
                        "category_id, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);",
                "Заголовок1", "Лид 2", Timestamp.valueOf(createDateArticle.atStartOfDay()),
                Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 2", true, 2, 2, 2);
        statement.executeUpdate(sqlInsertArticle2);
        String sqlInsertImages = String.format("INSERT INTO image (title, path, article_id) " +
                        "VALUES ('%s', '%s', %d), ('%s', '%s', %d), ('%s', '%s', %d), ('%s', '%s', %d);",
                "Изображение 1", "/static/images/image1.png", 1,
                "Изображение 2", "/static/images/image2.png", 1,
                "Изображение 3", "/static/images/image3.png", 2,
                "Изображение 4", "/static/images/image4.png", 2);
        statement.executeUpdate(sqlInsertImages);
        String sqlInsertTagsId = "INSERT INTO article_tag (article_id, tag_id) VALUES " +
                "(1, 1), (1, 2), (2, 3), (2, 4);";
        statement.executeUpdate(sqlInsertTagsId);
        // ожидаемый результат
        String expectedResult = "" +
                "HTTP/1.1 200 OK\n" +
                "Cache-Control: no-store, no-cache, must-revalidate\n" +
                "Pragma: no-cache\n" +
                "Content-Type: application/json; charset=UTF-8\n" +
                "\n" +
                "{\n" +
                "\t\"id\":2,\n" +
                "\t\"title\":\"Заголовок1\",\n" +
                "\t\"lead\":\"Лид 2\",\n" +
                "\t\"createDate\":1561410000,\n" +
                "\t\"editDate\":1561410000,\n" +
                "\t\"text\":\"Текст 2\",\n" +
                "\t\"isPublished\":true,\n" +
                "\t\"categoryId\":2,\n" +
                "\t\"userId\":2,\n" +
                "\t\"sourceId\":2,\n" +
                "\t\"images\":[\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":4,\n" +
                "\t\t\t\"title\":\"Изображение 4\",\n" +
                "\t\t\t\"path\":\"/static/images/image4.png\",\n" +
                "\t\t\t\"articleId\":2\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":3,\n" +
                "\t\t\t\"title\":\"Изображение 3\",\n" +
                "\t\t\t\"path\":\"/static/images/image3.png\",\n" +
                "\t\t\t\"articleId\":2\n" +
                "\t\t}\n" +
                "\t],\n" +
                "\t\"tagsId\":[\n" +
                "\t\t3,\n" +
                "\t\t4\n" +
                "\t]\n" +
                "}";
        clientSocket = new Socket("127.0.0.1", 5000);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));

        String request = "" +
                "GET /article/2/ HTTP/1.1\n" +
                "Accept: application/json, */*; q=0.01\n" +
                "Content-Type: application/json\n" +
                "Host: 127.0.0.1:5000\n" +
                "UnitTest: true\n" +
                "UrlPostgres: " + this.container.getJdbcUrl() + "\n" +
                "UserPostgres: " + this.container.getUsername() + "\n" +
                "PasswordPostgres: " + this.container.getPassword() + "\n";
        out.println(request);
        out.flush();

        StringBuilder actualResult = new StringBuilder();
        actualResult.append(in.readLine()).append("\n");
        while (in.ready()) {
            actualResult.append(in.readLine()).append("\n");
        }
        actualResult.setLength(actualResult.length() - 1);
        assertThat(actualResult.toString()).isEqualTo(expectedResult);
    }

    @Test
    void buildResponsePOSTMethod() throws SQLException, IOException {
        SoftAssertions soft = new SoftAssertions();
        // создаем две статьи с одиннаковым title
        Article article = new Article("Заголовок 10", "Лид 10", createDateArticle, editDateArticle,
                "Текст 10", true, 2, 2, 2);
        // добавляем к ним по 2 изображения
        Article.ArticleImage articleImage1 = new Article.ArticleImage("Изображение 10", "/static/images/image10.png", 1);
        Article.ArticleImage articleImage2 = new Article.ArticleImage("Изображение 11", "/static/images/image11.png", 1);
        article.addNewImage(articleImage1);
        article.addNewImage(articleImage2);
        article.addNewTagId(3);
        article.addNewTagId(4);
        clientSocket = new Socket("127.0.0.1", 5000);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));
        String expectedResult = "" +
            "HTTP/1.1 201 Статья создана\n" +
            "Cache-Control: no-store, no-cache, must-revalidate\n" +
            "Pragma: no-cache\n" +
            "Location: /article/1/\n";

        String request = "" +
            "POST /article/ HTTP/1.1\n" +
            "Accept: application/json, */*; q=0.01\n" +
            "Content-Type: application/json\n" +
            "Host: 127.0.0.1:5000\n" +
            "UnitTest: true\n" +
            "UrlPostgres: " + this.container.getJdbcUrl() + "\n" +
            "UserPostgres: " + this.container.getUsername() + "\n" +
            "PasswordPostgres: " + this.container.getPassword() + "\n" +
            "\n" +
            "{\n" +
            "\t\"title\":\"Заголовок 10\",\n" +
            "\t\"lead\":\"Лид 10\",\n" +
            "\t\"createDate\":1561410000,\n" +
            "\t\"editDate\":1561410000,\n" +
            "\t\"text\":\"Текст 10\",\n" +
            "\t\"isPublished\":true,\n" +
            "\t\"categoryId\":2,\n" +
            "\t\"userId\":2,\n" +
            "\t\"sourceId\":2,\n" +
            "\t\"images\":[\n" +
            "\t\t{\n" +
            "\t\t\t\"title\":\"Изображение 10\",\n" +
            "\t\t\t\"path\":\"/static/images/image10.png\",\n" +
            "\t\t\t\"articleId\":1\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"title\":\"Изображение 11\",\n" +
            "\t\t\t\"path\":\"/static/images/image11.png\",\n" +
            "\t\t\t\"articleId\":1\n" +
            "\t\t}\n" +
            "\t],\n" +
            "\t\"tagsId\":[\n" +
            "\t\t3,\n" +
            "\t\t4\n" +
            "\t]\n" +
            "}";
        out.println(request);
        out.flush();

        StringBuilder actualResult = new StringBuilder();
        actualResult.append(in.readLine()).append("\n");
        while (in.ready()) {
            actualResult.append(in.readLine()).append("\n");
        }
        actualResult.setLength(actualResult.length() - 1);
        // сначала сравниваем ответы
        assertThat(actualResult.toString()).isEqualTo(expectedResult);
        // сравниваем результаты
        String sqlQueryArticle = "SELECT * FROM article WHERE title='Заголовок 10';";
        Connection connection = poolConnection.getConnection();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sqlQueryArticle);
        result.next();
        soft.assertThat(article)
                .hasFieldOrPropertyWithValue("title", result.getString("title"))
                .hasFieldOrPropertyWithValue("lead", result.getString("lead"))
                .hasFieldOrPropertyWithValue("createDate", result.getTimestamp("create_date").toLocalDateTime().toLocalDate())
                .hasFieldOrPropertyWithValue("editDate", result.getTimestamp("edit_date").toLocalDateTime().toLocalDate())
                .hasFieldOrPropertyWithValue("text", result.getString("text"))
                .hasFieldOrPropertyWithValue("isPublished", result.getBoolean("is_published"))
                .hasFieldOrPropertyWithValue("categoryId", result.getInt("category_id"))
                .hasFieldOrPropertyWithValue("userId", result.getInt("user_id"))
                .hasFieldOrPropertyWithValue("sourceId", result.getInt("source_id"));
        soft.assertAll();
        String sqlQueryImages = "SELECT * FROM image WHERE article_id=1;";
        result = statement.executeQuery(sqlQueryImages);
        result.next();
        soft.assertThat(articleImage1)
                .hasFieldOrPropertyWithValue("title", result.getString("title"))
                .hasFieldOrPropertyWithValue("path", result.getString("path"))
                .hasFieldOrPropertyWithValue("articleId", result.getInt("article_id"));
        soft.assertAll();
        result.next();
        soft.assertThat(articleImage2)
                .hasFieldOrPropertyWithValue("title", result.getString("title"))
                .hasFieldOrPropertyWithValue("path", result.getString("path"))
                .hasFieldOrPropertyWithValue("articleId", result.getInt("article_id"));
        soft.assertAll();
        String sqlQueryTagsId = "SELECT * FROM article_tag WHERE article_id=1;";
        result = statement.executeQuery(sqlQueryTagsId);
        result.next();
        assertThat(result.getInt("tag_id")).isEqualTo(3);
        result.next();
        assertThat(result.getInt("tag_id")).isEqualTo(4);
    }

    @Test
    void buildResponsePUTMethod() throws IOException, SQLException {
        SoftAssertions soft = new SoftAssertions();
        Connection connection = this.poolConnection.getConnection();
        Statement statement = connection.createStatement();
        String sqlInsertArticle = String.format("INSERT INTO article (title, lead, create_date, edit_date, text, is_published, " +
                        "category_id, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);",
                "Заголовок1", "Лид 1", Timestamp.valueOf(createDateArticle.atStartOfDay()),
                Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 1", true, 1, 1, 1);
        statement.executeUpdate(sqlInsertArticle);
        String sqlInsertImages = String.format("INSERT INTO image (title, path, article_id) " +
                        "VALUES ('%s', '%s', %d), ('%s', '%s', %d);",
                "Изображение 1", "/static/images/image1.png", 1,
                "Изображение 2", "/static/images/image2.png", 1);
        statement.executeUpdate(sqlInsertImages);
        String sqlInsertTagsId = "INSERT INTO article_tag (article_id, tag_id) VALUES " +
                "(1, 1), (1, 2);";
        statement.executeUpdate(sqlInsertTagsId);
        Article article = new Article("Заголовок 2", "Лид 2", createDateArticle, editDateArticle,
                "Текст 2", false, 2, 2, 2);
        // добавляем к ним по 2 изображения
        Article.ArticleImage articleImage1 = new Article.ArticleImage("Изображение 3", "/static/images/image3.png", 1);
        Article.ArticleImage articleImage2 = new Article.ArticleImage("Изображение 4", "/static/images/image4.png", 1);
        article.addNewImage(articleImage1);
        article.addNewImage(articleImage2);
        article.addNewTagId(3);
        article.addNewTagId(4);
        clientSocket = new Socket("127.0.0.1", 5000);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));
        String expectedResult = "" +
                "HTTP/1.1 204 Нет данных\n" +
                "Cache-Control: no-store, no-cache, must-revalidate\n" +
                "Pragma: no-cache\n";

        String request = "" +
                "PUT /article/1/ HTTP/1.1\n" +
                "Accept: application/json, */*; q=0.01\n" +
                "Content-Type: application/json\n" +
                "Host: 127.0.0.1:5000\n" +
                "UnitTest: true\n" +
                "UrlPostgres: " + this.container.getJdbcUrl() + "\n" +
                "UserPostgres: " + this.container.getUsername() + "\n" +
                "PasswordPostgres: " + this.container.getPassword() + "\n" +
                "\n" +
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"title\":\"Заголовок 2\",\n" +
                "\t\"lead\":\"Лид 2\",\n" +
                "\t\"createDate\":1561410000,\n" +
                "\t\"editDate\":1561410000,\n" +
                "\t\"text\":\"Текст 2\",\n" +
                "\t\"isPublished\":false,\n" +
                "\t\"categoryId\":2,\n" +
                "\t\"userId\":2,\n" +
                "\t\"sourceId\":2,\n" +
                "\t\"images\":[\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":0,\n" +
                "\t\t\t\"title\":\"Изображение 3\",\n" +
                "\t\t\t\"path\":\"/static/images/image3.png\",\n" +
                "\t\t\t\"articleId\":1\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":0,\n" +
                "\t\t\t\"title\":\"Изображение 4\",\n" +
                "\t\t\t\"path\":\"/static/images/image4.png\",\n" +
                "\t\t\t\"articleId\":1\n" +
                "\t\t}\n" +
                "\t],\n" +
                "\t\"tagsId\":[\n" +
                "\t\t3,\n" +
                "\t\t4\n" +
                "\t]\n" +
                "}";
        out.println(request);
        out.flush();

        StringBuilder actualResult = new StringBuilder();
        actualResult.append(in.readLine()).append("\n");
        while (in.ready()) {
            actualResult.append(in.readLine()).append("\n");
        }
        actualResult.setLength(actualResult.length() - 1);
        // сначала сравниваем ответы
        assertThat(actualResult.toString()).isEqualTo(expectedResult);
        // сравниваем результаты
        String sqlQueryArticle = "SELECT * FROM article WHERE id=1;";
        connection = poolConnection.getConnection();
        statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sqlQueryArticle);
        result.next();
        soft.assertThat(article)
                .hasFieldOrPropertyWithValue("title", result.getString("title"))
                .hasFieldOrPropertyWithValue("lead", result.getString("lead"))
                .hasFieldOrPropertyWithValue("createDate", result.getTimestamp("create_date").toLocalDateTime().toLocalDate())
                .hasFieldOrPropertyWithValue("editDate", result.getTimestamp("edit_date").toLocalDateTime().toLocalDate())
                .hasFieldOrPropertyWithValue("text", result.getString("text"))
                .hasFieldOrPropertyWithValue("isPublished", result.getBoolean("is_published"))
                .hasFieldOrPropertyWithValue("categoryId", result.getInt("category_id"))
                .hasFieldOrPropertyWithValue("userId", result.getInt("user_id"))
                .hasFieldOrPropertyWithValue("sourceId", result.getInt("source_id"));
        soft.assertAll();
        String sqlQueryImages = "SELECT * FROM image WHERE article_id=1 ORDER BY title;";
        result = statement.executeQuery(sqlQueryImages);
        result.next();
        soft.assertThat(articleImage1)
                .hasFieldOrPropertyWithValue("title", result.getString("title"))
                .hasFieldOrPropertyWithValue("path", result.getString("path"))
                .hasFieldOrPropertyWithValue("articleId", result.getInt("article_id"));
        soft.assertAll();
        result.next();
        soft.assertThat(articleImage2)
                .hasFieldOrPropertyWithValue("title", result.getString("title"))
                .hasFieldOrPropertyWithValue("path", result.getString("path"))
                .hasFieldOrPropertyWithValue("articleId", result.getInt("article_id"));
        soft.assertAll();
        String sqlQueryTagsId = "SELECT * FROM article_tag WHERE article_id=1;";
        result = statement.executeQuery(sqlQueryTagsId);
        result.next();
        assertThat(result.getInt("tag_id")).isEqualTo(3);
        result.next();
        assertThat(result.getInt("tag_id")).isEqualTo(4);
    }

    @Test
    void buildResponseDELETEMethod() throws SQLException, IOException {
        Connection connection = this.poolConnection.getConnection();
        Statement statement = connection.createStatement();
        String sqlInsertArticle = String.format("INSERT INTO article (title, lead, create_date, edit_date, text, is_published, " +
                        "category_id, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);",
                "Заголовок1", "Лид 1", Timestamp.valueOf(createDateArticle.atStartOfDay()),
                Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 1", true, 1, 1, 1);
        statement.executeUpdate(sqlInsertArticle);
        String sqlInsertImages = String.format("INSERT INTO image (title, path, article_id) " +
                        "VALUES ('%s', '%s', %d), ('%s', '%s', %d);",
                "Изображение 1", "/static/images/image1.png", 1,
                "Изображение 2", "/static/images/image2.png", 1);
        statement.executeUpdate(sqlInsertImages);
        String sqlInsertTagsId = "INSERT INTO article_tag (article_id, tag_id) VALUES " +
                "(1, 1), (1, 2);";
        statement.executeUpdate(sqlInsertTagsId);

        clientSocket = new Socket("127.0.0.1", 5000);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));
        String expectedResult = "" +
                "HTTP/1.1 204 Нет данных\n" +
                "Cache-Control: no-store, no-cache, must-revalidate\n" +
                "Pragma: no-cache\n";

        String request = "" +
                "DELETE /article/1/ HTTP/1.1\n" +
                "Accept: application/json, */*; q=0.01\n" +
                "Content-Type: application/json\n" +
                "Host: 127.0.0.1:5000\n" +
                "UnitTest: true\n" +
                "UrlPostgres: " + this.container.getJdbcUrl() + "\n" +
                "UserPostgres: " + this.container.getUsername() + "\n" +
                "PasswordPostgres: " + this.container.getPassword() + "\n";
        out.println(request);
        out.flush();

        StringBuilder actualResult = new StringBuilder();
        actualResult.append(in.readLine()).append("\n");
        while (in.ready()) {
            actualResult.append(in.readLine()).append("\n");
        }
        actualResult.setLength(actualResult.length() - 1);
        // сначала сравниваем ответы
        assertThat(actualResult.toString()).isEqualTo(expectedResult);
        // сравниваем результаты из таблиц
        String sqlQueryArticle = "SELECT * FROM article WHERE title='Заголовок1';";
        ResultSet result = statement.executeQuery(sqlQueryArticle);
        assertThat(result.next()).isFalse().as("Не удалена статья по запросу");
        String sqlQueryImages = "SELECT * FROM image WHERE article_id=1;";
        result = statement.executeQuery(sqlQueryImages);
        assertThat(result.next()).isFalse().as("Не все записи изображений удалены по запросу");
        String sqlQueryTagsId = "SELECT * FROM article_tag WHERE article_id=1;";
        result = statement.executeQuery(sqlQueryTagsId);
        assertThat(result.next()).isFalse().as("Не все записи тегов удалены по запросу");
    }
}