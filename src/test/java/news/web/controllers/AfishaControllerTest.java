package news.web.controllers;

import news.dao.connection.DBPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AfishaControllerTest {
    private PostgreSQLContainer container;
    private DBPool poolConnection;
    private static LocalDate lastLogin;
    private static LocalDate dateJoined;
    private static LocalDate date;
    private static int userId;
    // для клиента
    private static Socket clientSocket;
    private static BufferedReader in;
    private static PrintWriter out;

    @BeforeAll
    static void beforeAll() {
        date = LocalDate.of(2020, 5, 20);
        lastLogin = LocalDate.of(2020, 5, 20);
        System.out.println(Timestamp.valueOf(lastLogin.atStartOfDay()).getTime() / 1000);
        dateJoined = LocalDate.of(2019, 5, 20);
        System.out.println(Timestamp.valueOf(dateJoined.atStartOfDay()).getTime() / 1000);
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
    void buildResponseGETMethodFindAll() throws IOException, SQLException {
        Connection connection = this.poolConnection.getConnection();
        Statement statement = connection.createStatement();
        String sqlInsertAfisha = String.format("INSERT INTO afisha (title, image_url, lead, description, age_limit, " +
                "timing, place, phone, date, is_commercial, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', " +
                "'%s', '%s', '%s', '%s', %s, %s, %s);", "title1", "image_url1", "lead1", "description1", "age1",
                "timing1", "place1", "phone1", Timestamp.valueOf(date.atStartOfDay()), true, 1, 1);
        statement.executeUpdate(sqlInsertAfisha);
        sqlInsertAfisha = String.format("INSERT INTO afisha (title, image_url, lead, description, age_limit, " +
                "timing, place, phone, date, is_commercial, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', " +
                "'%s', '%s', '%s', '%s', %s, %s, %s);", "title2", "image_url2", "lead2", "description2", "age2",
                "timing2", "place2", "phone2", Timestamp.valueOf(date.atStartOfDay()), true, 1, 1);
        statement.executeUpdate(sqlInsertAfisha);

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
            "\t\"title\":\"title1\",\n" +
            "\t\"imageUrl\":\"image_url1\",\n" +
            "\t\"lead\":\"lead1\",\n" +
            "\t\"description\":\"description1\",\n" +
            "\t\"ageLimit\":\"age1\",\n" +
            "\t\"timing\":\"timing1\",\n" +
            "\t\"place\":\"place1\",\n" +
            "\t\"phone\":\"phone1\",\n" +
            "\t\"date\":1589922000,\n" +
            "\t\"isCommercial\":true,\n" +
            "\t\"userId\":1,\n" +
            "\t\"sourceId\":1\n" +
            "},\n" +
            "{\n" +
            "\t\"id\":2,\n" +
            "\t\"title\":\"title2\",\n" +
            "\t\"imageUrl\":\"image_url2\",\n" +
            "\t\"lead\":\"lead2\",\n" +
            "\t\"description\":\"description2\",\n" +
            "\t\"ageLimit\":\"age2\",\n" +
            "\t\"timing\":\"timing2\",\n" +
            "\t\"place\":\"place2\",\n" +
            "\t\"phone\":\"phone2\",\n" +
            "\t\"date\":1589922000,\n" +
            "\t\"isCommercial\":true,\n" +
            "\t\"userId\":1,\n" +
            "\t\"sourceId\":1\n" +
            "}\n" +
            "]\n";
        clientSocket = new Socket("127.0.0.1", 5000);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));

        String request = "" +
                "GET /afisha/ HTTP/1.1\n" +
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

//    @Test
//    void buildResponseGETMethodFindByUserId() throws IOException, SQLException {
//        Connection connection = this.poolConnection.getConnection();
//        Statement statement = connection.createStatement();
//        // добавляем 1 комментарий
//        String sqlInsertComment = String.format("INSERT INTO comment (text, create_date, edit_date, article_id, user_id) " +
//                        "VALUES ('%s', '%s', '%s', %s, %s);", "Текст комментария", Timestamp.valueOf(createDateComment.atStartOfDay()),
//                Timestamp.valueOf(editDateComment.atStartOfDay()), 1, 1);
//        statement.executeUpdate(sqlInsertComment);
//        String sqlInsertAttachments = String.format("INSERT INTO attachment (title, path, comment_id)" +
//                        "VALUES ('%s', '%s', %s), ('%s', '%s', %s);", "Прикрепление 1", "/static/attachments/image1.png", 1,
//                "Прикрепление 2", "/static/attachments/image2.png", 1);
//        statement.executeUpdate(sqlInsertAttachments);
//        // добавляем 2 комментарий
//        sqlInsertComment = String.format("INSERT INTO comment (text, create_date, edit_date, article_id, user_id) " +
//                        "VALUES ('%s', '%s', '%s', %s, %s);", "Текст комментария 2", Timestamp.valueOf(createDateComment.atStartOfDay()),
//                Timestamp.valueOf(editDateComment.atStartOfDay()), 1, 1);
//        statement.executeUpdate(sqlInsertComment);
//        sqlInsertAttachments = String.format("INSERT INTO attachment (title, path, comment_id)" +
//                        "VALUES ('%s', '%s', %s), ('%s', '%s', %s);", "Прикрепление 3", "/static/attachments/image3.png", 2,
//                "Прикрепление 4", "/static/attachments/image4.png", 2);
//        statement.executeUpdate(sqlInsertAttachments);
//
//        // ожидаемый результат
//        String expectedResult = "" +
//                "HTTP/1.1 200 OK\n" +
//                "Cache-Control: no-store, no-cache, must-revalidate\n" +
//                "Pragma: no-cache\n" +
//                "Content-Type: application/json; charset=UTF-8\n" +
//                "\n" +
//                "[\n" +
//                "{\n" +
//                "\t\"id\":2,\n" +
//                "\t\"text\":\"Текст комментария 2\",\n" +
//                "\t\"createDate\":1558299600,\n" +
//                "\t\"editDate\":1589922000,\n" +
//                "\t\"userId\":1,\n" +
//                "\t\"articleId\":1,\n" +
//                "\t\"attachments\":[\n" +
//                "\t\t{\n" +
//                "\t\t\t\"id\":3,\n" +
//                "\t\t\t\"title\":\"Прикрепление 3\",\n" +
//                "\t\t\t\"path\":\"/static/attachments/image3.png\",\n" +
//                "\t\t\t\"commentId\":2\n" +
//                "\t\t},\n" +
//                "\t\t{\n" +
//                "\t\t\t\"id\":4,\n" +
//                "\t\t\t\"title\":\"Прикрепление 4\",\n" +
//                "\t\t\t\"path\":\"/static/attachments/image4.png\",\n" +
//                "\t\t\t\"commentId\":2\n" +
//                "\t\t}\n" +
//                "\t]\n" +
//                "},\n" +
//                "{\n" +
//                "\t\"id\":1,\n" +
//                "\t\"text\":\"Текст комментария\",\n" +
//                "\t\"createDate\":1558299600,\n" +
//                "\t\"editDate\":1589922000,\n" +
//                "\t\"userId\":1,\n" +
//                "\t\"articleId\":1,\n" +
//                "\t\"attachments\":[\n" +
//                "\t\t{\n" +
//                "\t\t\t\"id\":1,\n" +
//                "\t\t\t\"title\":\"Прикрепление 1\",\n" +
//                "\t\t\t\"path\":\"/static/attachments/image1.png\",\n" +
//                "\t\t\t\"commentId\":1\n" +
//                "\t\t},\n" +
//                "\t\t{\n" +
//                "\t\t\t\"id\":2,\n" +
//                "\t\t\t\"title\":\"Прикрепление 2\",\n" +
//                "\t\t\t\"path\":\"/static/attachments/image2.png\",\n" +
//                "\t\t\t\"commentId\":1\n" +
//                "\t\t}\n" +
//                "\t]\n" +
//                "}\n" +
//                "]\n";
//        clientSocket = new Socket("127.0.0.1", 5000);
//        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));
//
//        String request = "" +
//                "GET /comment?userid=1 HTTP/1.1\n" +
//                "Accept: application/json, */*; q=0.01\n" +
//                "Content-Type: application/json\n" +
//                "Host: 127.0.0.1:5000\n" +
//                "UnitTest: true\n" +
//                "UrlPostgres: " + this.container.getJdbcUrl() + "\n" +
//                "UserPostgres: " + this.container.getUsername() + "\n" +
//                "PasswordPostgres: " + this.container.getPassword() + "\n";
//        out.println(request);
//        out.flush();
//
//        StringBuilder actualResult = new StringBuilder();
//        actualResult.append(in.readLine()).append("\n");
//        while (in.ready()) {
//            actualResult.append(in.readLine()).append("\n");
//        }
//        actualResult.setLength(actualResult.length() - 1);
//        assertThat(actualResult.toString()).isEqualTo(expectedResult);
//    }

//    @Test
//    void buildResponseGETMethodFindById() throws SQLException, IOException {
//        Connection connection = this.poolConnection.getConnection();
//        Statement statement = connection.createStatement();
//        // добавляем 1 комментарий
//        String sqlInsertComment = String.format("INSERT INTO comment (text, create_date, edit_date, article_id, user_id) " +
//                        "VALUES ('%s', '%s', '%s', %s, %s);", "Текст комментария", Timestamp.valueOf(createDateComment.atStartOfDay()),
//                Timestamp.valueOf(editDateComment.atStartOfDay()), 1, 1);
//        statement.executeUpdate(sqlInsertComment);
//        String sqlInsertAttachments = String.format("INSERT INTO attachment (title, path, comment_id)" +
//                        "VALUES ('%s', '%s', %s), ('%s', '%s', %s);", "Прикрепление 1", "/static/attachments/image1.png", 1,
//                "Прикрепление 2", "/static/attachments/image2.png", 1);
//        statement.executeUpdate(sqlInsertAttachments);
//        // добавляем 2 комментарий
//        sqlInsertComment = String.format("INSERT INTO comment (text, create_date, edit_date, article_id, user_id) " +
//                        "VALUES ('%s', '%s', '%s', %s, %s);", "Текст комментария 2", Timestamp.valueOf(createDateComment.atStartOfDay()),
//                Timestamp.valueOf(editDateComment.atStartOfDay()), 1, 1);
//        statement.executeUpdate(sqlInsertComment);
//        sqlInsertAttachments = String.format("INSERT INTO attachment (title, path, comment_id)" +
//                        "VALUES ('%s', '%s', %s), ('%s', '%s', %s);", "Прикрепление 3", "/static/attachments/image3.png", 2,
//                "Прикрепление 4", "/static/attachments/image4.png", 2);
//        statement.executeUpdate(sqlInsertAttachments);
//
//        // ожидаемый результат
//        String expectedResult = "" +
//                "HTTP/1.1 200 OK\n" +
//                "Cache-Control: no-store, no-cache, must-revalidate\n" +
//                "Pragma: no-cache\n" +
//                "Content-Type: application/json; charset=UTF-8\n" +
//                "\n" +
//                "{\n" +
//                "\t\"id\":2,\n" +
//                "\t\"text\":\"Текст комментария 2\",\n" +
//                "\t\"createDate\":1558299600,\n" +
//                "\t\"editDate\":1589922000,\n" +
//                "\t\"userId\":1,\n" +
//                "\t\"articleId\":1,\n" +
//                "\t\"attachments\":[\n" +
//                "\t\t{\n" +
//                "\t\t\t\"id\":3,\n" +
//                "\t\t\t\"title\":\"Прикрепление 3\",\n" +
//                "\t\t\t\"path\":\"/static/attachments/image3.png\",\n" +
//                "\t\t\t\"commentId\":2\n" +
//                "\t\t},\n" +
//                "\t\t{\n" +
//                "\t\t\t\"id\":4,\n" +
//                "\t\t\t\"title\":\"Прикрепление 4\",\n" +
//                "\t\t\t\"path\":\"/static/attachments/image4.png\",\n" +
//                "\t\t\t\"commentId\":2\n" +
//                "\t\t}\n" +
//                "\t]\n" +
//                "}";
//        clientSocket = new Socket("127.0.0.1", 5000);
//        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));
//
//        String request = "" +
//                "GET /comment/2/ HTTP/1.1\n" +
//                "Accept: application/json, */*; q=0.01\n" +
//                "Content-Type: application/json\n" +
//                "Host: 127.0.0.1:5000\n" +
//                "UnitTest: true\n" +
//                "UrlPostgres: " + this.container.getJdbcUrl() + "\n" +
//                "UserPostgres: " + this.container.getUsername() + "\n" +
//                "PasswordPostgres: " + this.container.getPassword() + "\n";
//        out.println(request);
//        out.flush();
//
//        StringBuilder actualResult = new StringBuilder();
//        actualResult.append(in.readLine()).append("\n");
//        while (in.ready()) {
//            actualResult.append(in.readLine()).append("\n");
//        }
//        actualResult.setLength(actualResult.length() - 1);
//        assertThat(actualResult.toString()).isEqualTo(expectedResult);
//    }

//    @Test
//    void buildResponsePOSTMethod() throws SQLException, IOException {
//        SoftAssertions soft = new SoftAssertions();
//        Comment comment = new Comment("Текст комментария", createDateComment, editDateComment, 1, 1);
//        Comment.CommentAttachment commentAttachment1 = new Comment.CommentAttachment("Прикрепление 1", "/static/attachments/image1.png", 1);
//        Comment.CommentAttachment commentAttachment2 = new Comment.CommentAttachment("Прикрепление 2", "/static/attachments/image2.png", 1);
//        comment.addNewAttachment(commentAttachment1);
//        comment.addNewAttachment(commentAttachment2);
//
//        clientSocket = new Socket("127.0.0.1", 5000);
//        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));
//        String expectedResult = "" +
//            "HTTP/1.1 201 Комментарий создан\n" +
//            "Cache-Control: no-store, no-cache, must-revalidate\n" +
//            "Pragma: no-cache\n" +
//            "Location: /comment/1/\n";
//
//        String request = "" +
//            "POST /comment/ HTTP/1.1\n" +
//            "Accept: application/json, */*; q=0.01\n" +
//            "Content-Type: application/json\n" +
//            "Host: 127.0.0.1:5000\n" +
//            "UnitTest: true\n" +
//            "UrlPostgres: " + this.container.getJdbcUrl() + "\n" +
//            "UserPostgres: " + this.container.getUsername() + "\n" +
//            "PasswordPostgres: " + this.container.getPassword() + "\n" +
//            "\n" +
//            "{\n" +
//            "\t\"id\":1,\n" +
//            "\t\"text\":\"Текст комментария\",\n" +
//            "\t\"createDate\":1558299600,\n" +
//            "\t\"editDate\":1589922000,\n" +
//            "\t\"userId\":1,\n" +
//            "\t\"articleId\":1,\n" +
//            "\t\"attachments\":[\n" +
//            "\t\t{\n" +
//            "\t\t\t\"id\":0,\n" +
//            "\t\t\t\"title\":\"Прикрепление 1\",\n" +
//            "\t\t\t\"path\":\"/static/attachments/image1.png\",\n" +
//            "\t\t\t\"articleId\":1\n" +
//            "\t\t},\n" +
//            "\t\t{\n" +
//            "\t\t\t\"id\":0,\n" +
//            "\t\t\t\"title\":\"Прикрепление 2\",\n" +
//            "\t\t\t\"path\":\"/static/attachments/image2.png\",\n" +
//            "\t\t\t\"articleId\":1\n" +
//            "\t\t}\n" +
//            "\t]\n" +
//            "}";
//        out.println(request);
//        out.flush();
//
//        StringBuilder actualResult = new StringBuilder();
//        actualResult.append(in.readLine()).append("\n");
//        while (in.ready()) {
//            actualResult.append(in.readLine()).append("\n");
//        }
//        actualResult.setLength(actualResult.length() - 1);
//        // сначала сравниваем ответы
//        assertThat(actualResult.toString()).isEqualTo(expectedResult);
//        // сравниваем результаты
//        String sqlQueryComment = "SELECT * FROM comment WHERE id=1;";
//        Connection connection = poolConnection.getConnection();
//        Statement statement = connection.createStatement();
//        ResultSet result = statement.executeQuery(sqlQueryComment);
//        result.next();
//        soft.assertThat(comment)
//                .hasFieldOrPropertyWithValue("text", result.getString("text"))
//                .hasFieldOrPropertyWithValue("createDate", result.getTimestamp("create_date").toLocalDateTime().toLocalDate())
//                .hasFieldOrPropertyWithValue("editDate", result.getTimestamp("edit_date").toLocalDateTime().toLocalDate())
//                .hasFieldOrPropertyWithValue("articleId", result.getInt("article_id"))
//                .hasFieldOrPropertyWithValue("userId", result.getInt("user_id"));
//        soft.assertAll();
//        String sqlQueryAttachments = "SELECT * FROM attachment WHERE comment_id=1;";
//        result = statement.executeQuery(sqlQueryAttachments);
//        result.next();
//        soft.assertThat(commentAttachment1)
//                .hasFieldOrPropertyWithValue("title", result.getString("title"))
//                .hasFieldOrPropertyWithValue("path", result.getString("path"))
//                .hasFieldOrPropertyWithValue("commentId", result.getInt("comment_id"));
//        soft.assertAll();
//        result.next();
//        soft.assertThat(commentAttachment2)
//                .hasFieldOrPropertyWithValue("title", result.getString("title"))
//                .hasFieldOrPropertyWithValue("path", result.getString("path"))
//                .hasFieldOrPropertyWithValue("commentId", result.getInt("comment_id"));
//        soft.assertAll();
//    }
//
//    @Test
//    void buildResponsePUTMethod() throws IOException, SQLException {
//        SoftAssertions soft = new SoftAssertions();
//        Connection connection = this.poolConnection.getConnection();
//        Comment comment = new Comment("Текст комментария 10", createDateComment, editDateComment, 2, 1);
//        Comment.CommentAttachment commentAttachment1 = new Comment.CommentAttachment("Прикрепление 10", "/static/attachments/image10.png", 1);
//        Comment.CommentAttachment commentAttachment2 = new Comment.CommentAttachment("Прикрепление 11", "/static/attachments/image11.png", 1);
//        comment.addNewAttachment(commentAttachment1);
//        comment.addNewAttachment(commentAttachment2);
//
//        Statement statement = connection.createStatement();
//        // добавляем 1 комментарий
//        String sqlInsertComment = String.format("INSERT INTO comment (text, create_date, edit_date, article_id, user_id) " +
//                        "VALUES ('%s', '%s', '%s', %s, %s);", "Текст комментария", Timestamp.valueOf(createDateComment.atStartOfDay()),
//                Timestamp.valueOf(editDateComment.atStartOfDay()), 1, 1);
//        statement.executeUpdate(sqlInsertComment);
//        String sqlInsertAttachments = String.format("INSERT INTO attachment (title, path, comment_id)" +
//                        "VALUES ('%s', '%s', %s), ('%s', '%s', %s);", "Прикрепление 1", "/static/attachments/image1.png", 1,
//                "Прикрепление 2", "/static/attachments/image2.png", 1);
//        statement.executeUpdate(sqlInsertAttachments);
//        // добавляем 2 комментарий
//        sqlInsertComment = String.format("INSERT INTO comment (text, create_date, edit_date, article_id, user_id) " +
//                        "VALUES ('%s', '%s', '%s', %s, %s);", "Текст комментария 2", Timestamp.valueOf(createDateComment.atStartOfDay()),
//                Timestamp.valueOf(editDateComment.atStartOfDay()), 1, 1);
//        statement.executeUpdate(sqlInsertComment);
//        sqlInsertAttachments = String.format("INSERT INTO attachment (title, path, comment_id)" +
//                        "VALUES ('%s', '%s', %s), ('%s', '%s', %s);", "Прикрепление 3", "/static/attachments/image3.png", 2,
//                "Прикрепление 4", "/static/attachments/image4.png", 2);
//        statement.executeUpdate(sqlInsertAttachments);
//
//
//        clientSocket = new Socket("127.0.0.1", 5000);
//        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));
//        String expectedResult = "" +
//                "HTTP/1.1 204 Нет данных\n" +
//                "Cache-Control: no-store, no-cache, must-revalidate\n" +
//                "Pragma: no-cache\n";
//
//        String request = "" +
//                "PUT /comment/1/ HTTP/1.1\n" +
//                "Accept: application/json, */*; q=0.01\n" +
//                "Content-Type: application/json\n" +
//                "Host: 127.0.0.1:5000\n" +
//                "UnitTest: true\n" +
//                "UrlPostgres: " + this.container.getJdbcUrl() + "\n" +
//                "UserPostgres: " + this.container.getUsername() + "\n" +
//                "PasswordPostgres: " + this.container.getPassword() + "\n" +
//                "\n" +
//                "{\n" +
//                "\t\"id\":1,\n" +
//                "\t\"text\":\"Текст комментария 10\",\n" +
//                "\t\"createDate\":1558299600,\n" +
//                "\t\"editDate\":1589922000,\n" +
//                "\t\"userId\":2,\n" +
//                "\t\"articleId\":1,\n" +
//                "\t\"attachments\":[\n" +
//                "\t\t{\n" +
//                "\t\t\t\"id\":0,\n" +
//                "\t\t\t\"title\":\"Прикрепление 10\",\n" +
//                "\t\t\t\"path\":\"/static/attachments/image10.png\",\n" +
//                "\t\t\t\"articleId\":1\n" +
//                "\t\t},\n" +
//                "\t\t{\n" +
//                "\t\t\t\"id\":0,\n" +
//                "\t\t\t\"title\":\"Прикрепление 11\",\n" +
//                "\t\t\t\"path\":\"/static/attachments/image11.png\",\n" +
//                "\t\t\t\"articleId\":1\n" +
//                "\t\t}\n" +
//                "\t]\n" +
//                "}";
//        out.println(request);
//        out.flush();
//
//        StringBuilder actualResult = new StringBuilder();
//        actualResult.append(in.readLine()).append("\n");
//        while (in.ready()) {
//            actualResult.append(in.readLine()).append("\n");
//        }
//        actualResult.setLength(actualResult.length() - 1);
//        // сначала сравниваем ответы
//        assertThat(actualResult.toString()).isEqualTo(expectedResult);
//        // сравниваем результаты
//        String sqlQueryComment = "SELECT * FROM comment WHERE id=1;";
//        connection = poolConnection.getConnection();
//        statement = connection.createStatement();
//        ResultSet result = statement.executeQuery(sqlQueryComment);
//        result.next();
//        soft.assertThat(comment)
//                .hasFieldOrPropertyWithValue("text", result.getString("text"))
//                .hasFieldOrPropertyWithValue("createDate", result.getTimestamp("create_date").toLocalDateTime().toLocalDate())
//                .hasFieldOrPropertyWithValue("editDate", result.getTimestamp("edit_date").toLocalDateTime().toLocalDate())
//                .hasFieldOrPropertyWithValue("articleId", result.getInt("article_id"))
//                .hasFieldOrPropertyWithValue("userId", result.getInt("user_id"));
//        soft.assertAll();
//        String sqlQueryAttachments = "SELECT * FROM attachment WHERE comment_id=1;";
//        result = statement.executeQuery(sqlQueryAttachments);
//        result.next();
//        soft.assertThat(commentAttachment1)
//                .hasFieldOrPropertyWithValue("title", result.getString("title"))
//                .hasFieldOrPropertyWithValue("path", result.getString("path"))
//                .hasFieldOrPropertyWithValue("commentId", result.getInt("comment_id"));
//        soft.assertAll();
//        result.next();
//        soft.assertThat(commentAttachment2)
//                .hasFieldOrPropertyWithValue("title", result.getString("title"))
//                .hasFieldOrPropertyWithValue("path", result.getString("path"))
//                .hasFieldOrPropertyWithValue("commentId", result.getInt("comment_id"));
//        soft.assertAll();
//    }
//
//    @Test
//    void buildResponseDELETEMethod() throws SQLException, IOException {
//        Connection connection = this.poolConnection.getConnection();
//        Statement statement = connection.createStatement();
//        // добавляем 1 комментарий
//        String sqlInsertComment = String.format("INSERT INTO comment (text, create_date, edit_date, article_id, user_id) " +
//                        "VALUES ('%s', '%s', '%s', %s, %s);", "Текст комментария", Timestamp.valueOf(createDateComment.atStartOfDay()),
//                Timestamp.valueOf(editDateComment.atStartOfDay()), 1, 1);
//        statement.executeUpdate(sqlInsertComment);
//        String sqlInsertAttachments = String.format("INSERT INTO attachment (title, path, comment_id)" +
//                        "VALUES ('%s', '%s', %s), ('%s', '%s', %s);", "Прикрепление 1", "/static/attachments/image1.png", 1,
//                "Прикрепление 2", "/static/attachments/image2.png", 1);
//        statement.executeUpdate(sqlInsertAttachments);
//
//        clientSocket = new Socket("127.0.0.1", 5000);
//        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));
//        String expectedResult = "" +
//                "HTTP/1.1 204 Нет данных\n" +
//                "Cache-Control: no-store, no-cache, must-revalidate\n" +
//                "Pragma: no-cache\n";
//
//        String request = "" +
//                "DELETE /comment/1/ HTTP/1.1\n" +
//                "Accept: application/json, */*; q=0.01\n" +
//                "Content-Type: application/json\n" +
//                "Host: 127.0.0.1:5000\n" +
//                "UnitTest: true\n" +
//                "UrlPostgres: " + this.container.getJdbcUrl() + "\n" +
//                "UserPostgres: " + this.container.getUsername() + "\n" +
//                "PasswordPostgres: " + this.container.getPassword() + "\n";
//        out.println(request);
//        out.flush();
//
//        StringBuilder actualResult = new StringBuilder();
//        actualResult.append(in.readLine()).append("\n");
//        while (in.ready()) {
//            actualResult.append(in.readLine()).append("\n");
//        }
//        actualResult.setLength(actualResult.length() - 1);
//        // сначала сравниваем ответы
//        assertThat(actualResult.toString()).isEqualTo(expectedResult);
//        // сравниваем результаты из таблиц
//        String sqlQueryComment = "SELECT * FROM comment WHERE id=1;";
//        ResultSet result = statement.executeQuery(sqlQueryComment);
//        assertThat(result.next()).isFalse().as("Не удален комментарий по запросу");
//        String sqlQueryAttachments = "SELECT * FROM attachment WHERE comment_id=1;";
//        result = statement.executeQuery(sqlQueryAttachments);
//        assertThat(result.next()).isFalse().as("Не все записи прикреплений удалены по запросу");
//    }
}