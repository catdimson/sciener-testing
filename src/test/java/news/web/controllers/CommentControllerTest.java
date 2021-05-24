package news.web.controllers;

import news.dao.connection.DBPool;
import news.model.Comment;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.*;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CommentControllerTest {
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
    // для клиента
    private static Socket clientSocket;
    private static BufferedReader in;
    private static PrintWriter out;

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
    void buildResponseGETMethodFindAll() throws IOException, SQLException {
        Connection connection = this.poolConnection.getConnection();
        Statement statement = connection.createStatement();
        String sqlInsertComment = String.format("INSERT INTO comment (text, create_date, edit_date, article_id, user_id) " +
                        "VALUES ('%s', '%s', '%s', %s, %s);", "Текст комментария", Timestamp.valueOf(createDateComment.atStartOfDay()),
                Timestamp.valueOf(editDateComment.atStartOfDay()), 1, 1);
        statement.executeUpdate(sqlInsertComment);
        String sqlInsertAttachments = String.format("INSERT INTO attachment (title, path, comment_id)" +
                        "VALUES ('%s', '%s', %s), ('%s', '%s', %s);", "Прикрепление 1", "/static/attachments/image1.png", 1,
                "Прикрепление 2", "/static/attachments/image2.png", 1);
        statement.executeUpdate(sqlInsertAttachments);

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
            "\t\"text\":\"Текст комментария\",\n" +
            "\t\"createDate\":1558299600,\n" +
            "\t\"editDate\":1589922000,\n" +
            "\t\"userId\":1,\n" +
            "\t\"articleId\":1,\n" +
            "\t\"attachments\":[\n" +
            "\t\t{\n" +
            "\t\t\t\"id\":1,\n" +
            "\t\t\t\"title\":\"Прикрепление 1\",\n" +
            "\t\t\t\"path\":\"/static/attachments/image1.png\",\n" +
            "\t\t\t\"commentId\":1\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"id\":2,\n" +
            "\t\t\t\"title\":\"Прикрепление 2\",\n" +
            "\t\t\t\"path\":\"/static/attachments/image2.png\",\n" +
            "\t\t\t\"commentId\":1\n" +
            "\t\t}\n" +
            "\t]\n" +
            "}\n" +
            "]\n";
        clientSocket = new Socket("127.0.0.1", 5000);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));

        String request = "" +
                "GET /comment/ HTTP/1.1\n" +
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
    void buildResponseGETMethodFindByUserId() throws IOException, SQLException {
        Connection connection = this.poolConnection.getConnection();
        Statement statement = connection.createStatement();
        // добавляем 1 комментарий
        String sqlInsertComment = String.format("INSERT INTO comment (text, create_date, edit_date, article_id, user_id) " +
                        "VALUES ('%s', '%s', '%s', %s, %s);", "Текст комментария", Timestamp.valueOf(createDateComment.atStartOfDay()),
                Timestamp.valueOf(editDateComment.atStartOfDay()), 1, 1);
        statement.executeUpdate(sqlInsertComment);
        String sqlInsertAttachments = String.format("INSERT INTO attachment (title, path, comment_id)" +
                        "VALUES ('%s', '%s', %s), ('%s', '%s', %s);", "Прикрепление 1", "/static/attachments/image1.png", 1,
                "Прикрепление 2", "/static/attachments/image2.png", 1);
        statement.executeUpdate(sqlInsertAttachments);
        // добавляем 2 комментарий
        sqlInsertComment = String.format("INSERT INTO comment (text, create_date, edit_date, article_id, user_id) " +
                        "VALUES ('%s', '%s', '%s', %s, %s);", "Текст комментария 2", Timestamp.valueOf(createDateComment.atStartOfDay()),
                Timestamp.valueOf(editDateComment.atStartOfDay()), 1, 1);
        statement.executeUpdate(sqlInsertComment);
        sqlInsertAttachments = String.format("INSERT INTO attachment (title, path, comment_id)" +
                        "VALUES ('%s', '%s', %s), ('%s', '%s', %s);", "Прикрепление 3", "/static/attachments/image3.png", 2,
                "Прикрепление 4", "/static/attachments/image4.png", 2);
        statement.executeUpdate(sqlInsertAttachments);

        // ожидаемый результат
        String expectedResult = "" +
                "HTTP/1.1 200 OK\n" +
                "Cache-Control: no-store, no-cache, must-revalidate\n" +
                "Pragma: no-cache\n" +
                "Content-Type: application/json; charset=UTF-8\n" +
                "\n" +
                "[\n" +
                "{\n" +
                "\t\"id\":2,\n" +
                "\t\"text\":\"Текст комментария 2\",\n" +
                "\t\"createDate\":1558299600,\n" +
                "\t\"editDate\":1589922000,\n" +
                "\t\"userId\":1,\n" +
                "\t\"articleId\":1,\n" +
                "\t\"attachments\":[\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":3,\n" +
                "\t\t\t\"title\":\"Прикрепление 3\",\n" +
                "\t\t\t\"path\":\"/static/attachments/image3.png\",\n" +
                "\t\t\t\"commentId\":2\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":4,\n" +
                "\t\t\t\"title\":\"Прикрепление 4\",\n" +
                "\t\t\t\"path\":\"/static/attachments/image4.png\",\n" +
                "\t\t\t\"commentId\":2\n" +
                "\t\t}\n" +
                "\t]\n" +
                "},\n" +
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"text\":\"Текст комментария\",\n" +
                "\t\"createDate\":1558299600,\n" +
                "\t\"editDate\":1589922000,\n" +
                "\t\"userId\":1,\n" +
                "\t\"articleId\":1,\n" +
                "\t\"attachments\":[\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":1,\n" +
                "\t\t\t\"title\":\"Прикрепление 1\",\n" +
                "\t\t\t\"path\":\"/static/attachments/image1.png\",\n" +
                "\t\t\t\"commentId\":1\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":2,\n" +
                "\t\t\t\"title\":\"Прикрепление 2\",\n" +
                "\t\t\t\"path\":\"/static/attachments/image2.png\",\n" +
                "\t\t\t\"commentId\":1\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}\n" +
                "]\n";
        clientSocket = new Socket("127.0.0.1", 5000);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));

        String request = "" +
                "GET /comment?userid=1 HTTP/1.1\n" +
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
        Connection connection = this.poolConnection.getConnection();
        Statement statement = connection.createStatement();
        // добавляем 1 комментарий
        String sqlInsertComment = String.format("INSERT INTO comment (text, create_date, edit_date, article_id, user_id) " +
                        "VALUES ('%s', '%s', '%s', %s, %s);", "Текст комментария", Timestamp.valueOf(createDateComment.atStartOfDay()),
                Timestamp.valueOf(editDateComment.atStartOfDay()), 1, 1);
        statement.executeUpdate(sqlInsertComment);
        String sqlInsertAttachments = String.format("INSERT INTO attachment (title, path, comment_id)" +
                        "VALUES ('%s', '%s', %s), ('%s', '%s', %s);", "Прикрепление 1", "/static/attachments/image1.png", 1,
                "Прикрепление 2", "/static/attachments/image2.png", 1);
        statement.executeUpdate(sqlInsertAttachments);
        // добавляем 2 комментарий
        sqlInsertComment = String.format("INSERT INTO comment (text, create_date, edit_date, article_id, user_id) " +
                        "VALUES ('%s', '%s', '%s', %s, %s);", "Текст комментария 2", Timestamp.valueOf(createDateComment.atStartOfDay()),
                Timestamp.valueOf(editDateComment.atStartOfDay()), 1, 1);
        statement.executeUpdate(sqlInsertComment);
        sqlInsertAttachments = String.format("INSERT INTO attachment (title, path, comment_id)" +
                        "VALUES ('%s', '%s', %s), ('%s', '%s', %s);", "Прикрепление 3", "/static/attachments/image3.png", 2,
                "Прикрепление 4", "/static/attachments/image4.png", 2);
        statement.executeUpdate(sqlInsertAttachments);

        // ожидаемый результат
        String expectedResult = "" +
                "HTTP/1.1 200 OK\n" +
                "Cache-Control: no-store, no-cache, must-revalidate\n" +
                "Pragma: no-cache\n" +
                "Content-Type: application/json; charset=UTF-8\n" +
                "\n" +
                "{\n" +
                "\t\"id\":2,\n" +
                "\t\"text\":\"Текст комментария 2\",\n" +
                "\t\"createDate\":1558299600,\n" +
                "\t\"editDate\":1589922000,\n" +
                "\t\"userId\":1,\n" +
                "\t\"articleId\":1,\n" +
                "\t\"attachments\":[\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":3,\n" +
                "\t\t\t\"title\":\"Прикрепление 3\",\n" +
                "\t\t\t\"path\":\"/static/attachments/image3.png\",\n" +
                "\t\t\t\"commentId\":2\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":4,\n" +
                "\t\t\t\"title\":\"Прикрепление 4\",\n" +
                "\t\t\t\"path\":\"/static/attachments/image4.png\",\n" +
                "\t\t\t\"commentId\":2\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}";
        clientSocket = new Socket("127.0.0.1", 5000);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));

        String request = "" +
                "GET /comment/2/ HTTP/1.1\n" +
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
        Comment comment = new Comment("Текст комментария", createDateComment, editDateComment, 1, 1);
        Comment.CommentAttachment commentAttachment1 = new Comment.CommentAttachment("Прикрепление 1", "/static/attachments/image1.png", 1);
        Comment.CommentAttachment commentAttachment2 = new Comment.CommentAttachment("Прикрепление 2", "/static/attachments/image2.png", 1);
        comment.addNewAttachment(commentAttachment1);
        comment.addNewAttachment(commentAttachment2);

        clientSocket = new Socket("127.0.0.1", 5000);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));
        String expectedResult = "" +
            "HTTP/1.1 201 Комментарий создан\n" +
            "Cache-Control: no-store, no-cache, must-revalidate\n" +
            "Pragma: no-cache\n" +
            "Location: /comment/1/\n";

        String request = "" +
            "POST /comment/ HTTP/1.1\n" +
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
            "\t\"text\":\"Текст комментария\",\n" +
            "\t\"createDate\":1558299600,\n" +
            "\t\"editDate\":1589922000,\n" +
            "\t\"userId\":1,\n" +
            "\t\"articleId\":1,\n" +
            "\t\"attachments\":[\n" +
            "\t\t{\n" +
            "\t\t\t\"id\":0,\n" +
            "\t\t\t\"title\":\"Прикрепление 1\",\n" +
            "\t\t\t\"path\":\"/static/attachments/image1.png\",\n" +
            "\t\t\t\"articleId\":1\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"id\":0,\n" +
            "\t\t\t\"title\":\"Прикрепление 2\",\n" +
            "\t\t\t\"path\":\"/static/attachments/image2.png\",\n" +
            "\t\t\t\"articleId\":1\n" +
            "\t\t}\n" +
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
        String sqlQueryComment = "SELECT * FROM comment WHERE id=1;";
        Connection connection = poolConnection.getConnection();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sqlQueryComment);
        result.next();
        soft.assertThat(comment)
                .hasFieldOrPropertyWithValue("text", result.getString("text"))
                .hasFieldOrPropertyWithValue("createDate", result.getTimestamp("create_date").toLocalDateTime().toLocalDate())
                .hasFieldOrPropertyWithValue("editDate", result.getTimestamp("edit_date").toLocalDateTime().toLocalDate())
                .hasFieldOrPropertyWithValue("articleId", result.getInt("article_id"))
                .hasFieldOrPropertyWithValue("userId", result.getInt("user_id"));
        soft.assertAll();
        String sqlQueryAttachments = "SELECT * FROM attachment WHERE comment_id=1 ORDER BY title;";
        result = statement.executeQuery(sqlQueryAttachments);
        result.next();
        soft.assertThat(commentAttachment1)
                .hasFieldOrPropertyWithValue("title", result.getString("title"))
                .hasFieldOrPropertyWithValue("path", result.getString("path"))
                .hasFieldOrPropertyWithValue("commentId", result.getInt("comment_id"));
        soft.assertAll();
        result.next();
        soft.assertThat(commentAttachment2)
                .hasFieldOrPropertyWithValue("title", result.getString("title"))
                .hasFieldOrPropertyWithValue("path", result.getString("path"))
                .hasFieldOrPropertyWithValue("commentId", result.getInt("comment_id"));
        soft.assertAll();
    }

    @Test
    void buildResponsePUTMethod() throws IOException, SQLException {
        SoftAssertions soft = new SoftAssertions();
        Connection connection = this.poolConnection.getConnection();
        Comment comment = new Comment("Текст комментария 10", createDateComment, editDateComment, 2, 1);
        Comment.CommentAttachment commentAttachment1 = new Comment.CommentAttachment("Прикрепление 10", "/static/attachments/image10.png", 1);
        Comment.CommentAttachment commentAttachment2 = new Comment.CommentAttachment("Прикрепление 11", "/static/attachments/image11.png", 1);
        comment.addNewAttachment(commentAttachment1);
        comment.addNewAttachment(commentAttachment2);

        Statement statement = connection.createStatement();
        // добавляем 1 комментарий
        String sqlInsertComment = String.format("INSERT INTO comment (text, create_date, edit_date, article_id, user_id) " +
                        "VALUES ('%s', '%s', '%s', %s, %s);", "Текст комментария", Timestamp.valueOf(createDateComment.atStartOfDay()),
                Timestamp.valueOf(editDateComment.atStartOfDay()), 1, 1);
        statement.executeUpdate(sqlInsertComment);
        String sqlInsertAttachments = String.format("INSERT INTO attachment (title, path, comment_id)" +
                        "VALUES ('%s', '%s', %s), ('%s', '%s', %s);", "Прикрепление 1", "/static/attachments/image1.png", 1,
                "Прикрепление 2", "/static/attachments/image2.png", 1);
        statement.executeUpdate(sqlInsertAttachments);
        // добавляем 2 комментарий
        sqlInsertComment = String.format("INSERT INTO comment (text, create_date, edit_date, article_id, user_id) " +
                        "VALUES ('%s', '%s', '%s', %s, %s);", "Текст комментария 2", Timestamp.valueOf(createDateComment.atStartOfDay()),
                Timestamp.valueOf(editDateComment.atStartOfDay()), 1, 1);
        statement.executeUpdate(sqlInsertComment);
        sqlInsertAttachments = String.format("INSERT INTO attachment (title, path, comment_id)" +
                        "VALUES ('%s', '%s', %s), ('%s', '%s', %s);", "Прикрепление 3", "/static/attachments/image3.png", 2,
                "Прикрепление 4", "/static/attachments/image4.png", 2);
        statement.executeUpdate(sqlInsertAttachments);


        clientSocket = new Socket("127.0.0.1", 5000);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));
        String expectedResult = "" +
                "HTTP/1.1 204 Нет данных\n" +
                "Cache-Control: no-store, no-cache, must-revalidate\n" +
                "Pragma: no-cache\n";

        String request = "" +
                "PUT /comment/1/ HTTP/1.1\n" +
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
                "\t\"text\":\"Текст комментария 10\",\n" +
                "\t\"createDate\":1558299600,\n" +
                "\t\"editDate\":1589922000,\n" +
                "\t\"userId\":2,\n" +
                "\t\"articleId\":1,\n" +
                "\t\"attachments\":[\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":0,\n" +
                "\t\t\t\"title\":\"Прикрепление 10\",\n" +
                "\t\t\t\"path\":\"/static/attachments/image10.png\",\n" +
                "\t\t\t\"articleId\":1\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":0,\n" +
                "\t\t\t\"title\":\"Прикрепление 11\",\n" +
                "\t\t\t\"path\":\"/static/attachments/image11.png\",\n" +
                "\t\t\t\"articleId\":1\n" +
                "\t\t}\n" +
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
        String sqlQueryComment = "SELECT * FROM comment WHERE id=1;";
        connection = poolConnection.getConnection();
        statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sqlQueryComment);
        result.next();
        soft.assertThat(comment)
                .hasFieldOrPropertyWithValue("text", result.getString("text"))
                .hasFieldOrPropertyWithValue("createDate", result.getTimestamp("create_date").toLocalDateTime().toLocalDate())
                .hasFieldOrPropertyWithValue("editDate", result.getTimestamp("edit_date").toLocalDateTime().toLocalDate())
                .hasFieldOrPropertyWithValue("articleId", result.getInt("article_id"))
                .hasFieldOrPropertyWithValue("userId", result.getInt("user_id"));
        soft.assertAll();
        String sqlQueryAttachments = "SELECT * FROM attachment WHERE comment_id=1;";
        result = statement.executeQuery(sqlQueryAttachments);
        result.next();
        soft.assertThat(commentAttachment1)
                .hasFieldOrPropertyWithValue("title", result.getString("title"))
                .hasFieldOrPropertyWithValue("path", result.getString("path"))
                .hasFieldOrPropertyWithValue("commentId", result.getInt("comment_id"));
        soft.assertAll();
        result.next();
        soft.assertThat(commentAttachment2)
                .hasFieldOrPropertyWithValue("title", result.getString("title"))
                .hasFieldOrPropertyWithValue("path", result.getString("path"))
                .hasFieldOrPropertyWithValue("commentId", result.getInt("comment_id"));
        soft.assertAll();
    }

    @Test
    void buildResponseDELETEMethod() throws SQLException, IOException {
        Connection connection = this.poolConnection.getConnection();
        Statement statement = connection.createStatement();
        // добавляем 1 комментарий
        String sqlInsertComment = String.format("INSERT INTO comment (text, create_date, edit_date, article_id, user_id) " +
                        "VALUES ('%s', '%s', '%s', %s, %s);", "Текст комментария", Timestamp.valueOf(createDateComment.atStartOfDay()),
                Timestamp.valueOf(editDateComment.atStartOfDay()), 1, 1);
        statement.executeUpdate(sqlInsertComment);
        String sqlInsertAttachments = String.format("INSERT INTO attachment (title, path, comment_id)" +
                        "VALUES ('%s', '%s', %s), ('%s', '%s', %s);", "Прикрепление 1", "/static/attachments/image1.png", 1,
                "Прикрепление 2", "/static/attachments/image2.png", 1);
        statement.executeUpdate(sqlInsertAttachments);

        clientSocket = new Socket("127.0.0.1", 5000);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));
        String expectedResult = "" +
                "HTTP/1.1 204 Нет данных\n" +
                "Cache-Control: no-store, no-cache, must-revalidate\n" +
                "Pragma: no-cache\n";

        String request = "" +
                "DELETE /comment/1/ HTTP/1.1\n" +
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
        String sqlQueryComment = "SELECT * FROM comment WHERE id=1;";
        ResultSet result = statement.executeQuery(sqlQueryComment);
        assertThat(result.next()).isFalse().as("Не удален комментарий по запросу");
        String sqlQueryAttachments = "SELECT * FROM attachment WHERE comment_id=1;";
        result = statement.executeQuery(sqlQueryAttachments);
        assertThat(result.next()).isFalse().as("Не все записи прикреплений удалены по запросу");
    }
}