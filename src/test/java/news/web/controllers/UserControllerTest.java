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

class UserControllerTest {
    private PostgreSQLContainer container;
    private DBPool poolConnection;
    private static LocalDate lastLogin;
    private static LocalDate dateJoined;
    private static int groupId;
    // для клиента
    private static Socket clientSocket;
    private static BufferedReader in;
    private static PrintWriter out;

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
    void buildResponseGETMethodFindAll() throws IOException, SQLException {
        Connection connection = this.poolConnection.getConnection();
        Statement statement = connection.createStatement();
        String sqlInsertUser = String.format("INSERT INTO \"user\" (password, username, first_name, last_name, email, " +
                "last_login, date_joined, is_superuser, is_staff, is_active, group_id) VALUES ('%s', '%s', '%s', '%s', " +
                "'%s', '%s', '%s', %s, %s, %s, %s);", "password1", "username1", "firstname1", "lastname1", "email1@mail.ru",
                Timestamp.valueOf(lastLogin.atStartOfDay()), Timestamp.valueOf(dateJoined.atStartOfDay()), true, true, true, 1);
        statement.executeUpdate(sqlInsertUser);
        sqlInsertUser = String.format("INSERT INTO \"user\" (password, username, first_name, last_name, email, " +
                "last_login, date_joined, is_superuser, is_staff, is_active, group_id) VALUES ('%s', '%s', '%s', '%s', " +
                "'%s', '%s', '%s', %s, %s, %s, %s);", "password2", "username2", "firstname2", "lastname2", "email2@mail.ru",
                Timestamp.valueOf(lastLogin.atStartOfDay()), Timestamp.valueOf(dateJoined.atStartOfDay()), false, true, true, 1);
        statement.executeUpdate(sqlInsertUser);

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
            "\t\"password\":\"password1\",\n" +
            "\t\"username\":\"username1\",\n" +
            "\t\"firstName\":\"firstname1\",\n" +
            "\t\"lastName\":\"lastname1\",\n" +
            "\t\"email\":\"email1@mail.ru\",\n" +
            "\t\"lastLogin\":1589922000,\n" +
            "\t\"dateJoined\":1558299600,\n" +
            "\t\"isSuperuser\":true,\n" +
            "\t\"isStaff\":true,\n" +
            "\t\"isActive\":true,\n" +
            "\t\"groupId\":1\n" +
            "},\n" +
            "{\n" +
            "\t\"id\":2,\n" +
            "\t\"password\":\"password2\",\n" +
            "\t\"username\":\"username2\",\n" +
            "\t\"firstName\":\"firstname2\",\n" +
            "\t\"lastName\":\"lastname2\",\n" +
            "\t\"email\":\"email2@mail.ru\",\n" +
            "\t\"lastLogin\":1589922000,\n" +
            "\t\"dateJoined\":1558299600,\n" +
            "\t\"isSuperuser\":false,\n" +
            "\t\"isStaff\":true,\n" +
            "\t\"isActive\":true,\n" +
            "\t\"groupId\":1\n" +
            "}\n" +
            "]\n";
        clientSocket = new Socket("127.0.0.1", 5000);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));

        String request = "" +
                "GET /user/ HTTP/1.1\n" +
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
    void buildResponseGETMethodFindByFirstname() throws IOException, SQLException {
        Connection connection = this.poolConnection.getConnection();
        Statement statement = connection.createStatement();
        String sqlInsertUser = String.format("INSERT INTO \"user\" (password, username, first_name, last_name, email, " +
                        "last_login, date_joined, is_superuser, is_staff, is_active, group_id) VALUES ('%s', '%s', '%s', '%s', " +
                        "'%s', '%s', '%s', %s, %s, %s, %s);", "password1", "username1", "firstname1", "lastname1", "email1@mail.ru",
                Timestamp.valueOf(lastLogin.atStartOfDay()), Timestamp.valueOf(dateJoined.atStartOfDay()), true, true, true, 1);
        statement.executeUpdate(sqlInsertUser);
        sqlInsertUser = String.format("INSERT INTO \"user\" (password, username, first_name, last_name, email, " +
                        "last_login, date_joined, is_superuser, is_staff, is_active, group_id) VALUES ('%s', '%s', '%s', '%s', " +
                        "'%s', '%s', '%s', %s, %s, %s, %s);", "password2", "username2", "firstname1", "lastname2", "email2@mail.ru",
                Timestamp.valueOf(lastLogin.atStartOfDay()), Timestamp.valueOf(dateJoined.atStartOfDay()), false, true, true, 1);
        statement.executeUpdate(sqlInsertUser);

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
                "\t\"password\":\"password1\",\n" +
                "\t\"username\":\"username1\",\n" +
                "\t\"firstName\":\"firstname1\",\n" +
                "\t\"lastName\":\"lastname1\",\n" +
                "\t\"email\":\"email1@mail.ru\",\n" +
                "\t\"lastLogin\":1589922000,\n" +
                "\t\"dateJoined\":1558299600,\n" +
                "\t\"isSuperuser\":true,\n" +
                "\t\"isStaff\":true,\n" +
                "\t\"isActive\":true,\n" +
                "\t\"groupId\":1\n" +
                "},\n" +
                "{\n" +
                "\t\"id\":2,\n" +
                "\t\"password\":\"password2\",\n" +
                "\t\"username\":\"username2\",\n" +
                "\t\"firstName\":\"firstname1\",\n" +
                "\t\"lastName\":\"lastname2\",\n" +
                "\t\"email\":\"email2@mail.ru\",\n" +
                "\t\"lastLogin\":1589922000,\n" +
                "\t\"dateJoined\":1558299600,\n" +
                "\t\"isSuperuser\":false,\n" +
                "\t\"isStaff\":true,\n" +
                "\t\"isActive\":true,\n" +
                "\t\"groupId\":1\n" +
                "}\n" +
                "]\n";
        clientSocket = new Socket("127.0.0.1", 5000);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));

        String request = "" +
                "GET /user?firstname=firstname1 HTTP/1.1\n" +
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
//    void buildResponseGETMethodFindById() throws SQLException, IOException {
//        Connection connection = this.poolConnection.getConnection();
//        Statement statement = connection.createStatement();
//        String sqlInsertAfisha = String.format("INSERT INTO afisha (title, image_url, lead, description, age_limit, " +
//                        "timing, place, phone, date, is_commercial, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', " +
//                        "'%s', '%s', '%s', '%s', %s, %s, %s);", "title1", "image_url1", "lead1", "description1", "age1",
//                "timing1", "place1", "phone1", Timestamp.valueOf(date.atStartOfDay()), true, 1, 1);
//        statement.executeUpdate(sqlInsertAfisha);
//        sqlInsertAfisha = String.format("INSERT INTO afisha (title, image_url, lead, description, age_limit, " +
//                        "timing, place, phone, date, is_commercial, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', " +
//                        "'%s', '%s', '%s', '%s', %s, %s, %s);", "title1", "image_url2", "lead2", "description2", "age2",
//                "timing2", "place2", "phone2", Timestamp.valueOf(date.atStartOfDay()), true, 1, 1);
//        statement.executeUpdate(sqlInsertAfisha);
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
//                "\t\"title\":\"title1\",\n" +
//                "\t\"imageUrl\":\"image_url2\",\n" +
//                "\t\"lead\":\"lead2\",\n" +
//                "\t\"description\":\"description2\",\n" +
//                "\t\"ageLimit\":\"age2\",\n" +
//                "\t\"timing\":\"timing2\",\n" +
//                "\t\"place\":\"place2\",\n" +
//                "\t\"phone\":\"phone2\",\n" +
//                "\t\"date\":1589922000,\n" +
//                "\t\"isCommercial\":true,\n" +
//                "\t\"userId\":1,\n" +
//                "\t\"sourceId\":1\n" +
//                "}";
//        clientSocket = new Socket("127.0.0.1", 5000);
//        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));
//
//        String request = "" +
//                "GET /afisha/2/ HTTP/1.1\n" +
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
//        Afisha afisha = new Afisha("Масленица", "/media/maslenica.jpg", "Празничные гуляния на площади", "Описание масленичных гуляний",
//                "0", "180", "Центральная площадь, г.Белгород", "89202005544", date, false, 1, 1);
//
//        clientSocket = new Socket("127.0.0.1", 5000);
//        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));
//        String expectedResult = "" +
//            "HTTP/1.1 201 Афиша создана\n" +
//            "Cache-Control: no-store, no-cache, must-revalidate\n" +
//            "Pragma: no-cache\n" +
//            "Location: /afisha/1/\n";
//
//        String request = "" +
//            "POST /afisha/ HTTP/1.1\n" +
//            "Accept: application/json, */*; q=0.01\n" +
//            "Content-Type: application/json\n" +
//            "Host: 127.0.0.1:5000\n" +
//            "UnitTest: true\n" +
//            "UrlPostgres: " + this.container.getJdbcUrl() + "\n" +
//            "UserPostgres: " + this.container.getUsername() + "\n" +
//            "PasswordPostgres: " + this.container.getPassword() + "\n" +
//            "\n" +
//            "{\n" +
//            "\t\"title\":\"Масленица\",\n" +
//            "\t\"imageUrl\":\"/media/maslenica.jpg\",\n" +
//            "\t\"lead\":\"Празничные гуляния на площади\",\n" +
//            "\t\"description\":\"Описание масленичных гуляний\",\n" +
//            "\t\"ageLimit\":\"0\",\n" +
//            "\t\"timing\":\"180\",\n" +
//            "\t\"place\":\"Центральная площадь, г.Белгород\",\n" +
//            "\t\"phone\":\"89202005544\",\n" +
//            "\t\"date\":1592600400,\n" +
//            "\t\"isCommercial\":false,\n" +
//            "\t\"userId\":1,\n" +
//            "\t\"sourceId\":1\n" +
//            "}\n";
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
//        String sqlQueryAfishat = "SELECT * FROM afisha WHERE id=1;";
//        Connection connection = poolConnection.getConnection();
//        Statement statement = connection.createStatement();
//        ResultSet result = statement.executeQuery(sqlQueryAfishat);
//        result.next();
//        soft.assertThat(afisha)
//                .hasFieldOrPropertyWithValue("title", result.getString("title"))
//                .hasFieldOrPropertyWithValue("imageUrl", result.getString("image_url"))
//                .hasFieldOrPropertyWithValue("lead", result.getString("lead"))
//                .hasFieldOrPropertyWithValue("description", result.getString("description"))
//                .hasFieldOrPropertyWithValue("ageLimit", result.getString("age_limit"))
//                .hasFieldOrPropertyWithValue("timing", result.getString("timing"))
//                .hasFieldOrPropertyWithValue("place", result.getString("place"))
//                .hasFieldOrPropertyWithValue("phone", result.getString("phone"))
//                .hasFieldOrPropertyWithValue("date", result.getTimestamp("date").toLocalDateTime().toLocalDate())
//                .hasFieldOrPropertyWithValue("isCommercial", result.getBoolean("is_commercial"))
//                .hasFieldOrPropertyWithValue("userId", result.getInt("user_id"))
//                .hasFieldOrPropertyWithValue("sourceId", result.getInt("source_id"));
//        soft.assertAll();
//    }

//    @Test
//    void buildResponsePUTMethod() throws IOException, SQLException {
//        SoftAssertions soft = new SoftAssertions();
//        Connection connection = this.poolConnection.getConnection();
//        Afisha afisha = new Afisha("Масленица", "/media/maslenica.jpg", "Празничные гуляния на площади", "Описание масленичных гуляний",
//                "0", "180", "Центральная площадь, г.Белгород", "89202005544", date, false, 1, 1);
//        Statement statement = connection.createStatement();
//        String sqlInsertAfisha = String.format("INSERT INTO afisha (title, image_url, lead, description, age_limit, " +
//                        "timing, place, phone, date, is_commercial, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', " +
//                        "'%s', '%s', '%s', '%s', %s, %s, %s);", "title1", "image_url1", "lead1", "description1", "age1",
//                "timing1", "place1", "phone1", Timestamp.valueOf(date.atStartOfDay()), true, 1, 1);
//        statement.executeUpdate(sqlInsertAfisha);
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
//                "PUT /afisha/1/ HTTP/1.1\n" +
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
//                "\t\"title\":\"Масленица\",\n" +
//                "\t\"imageUrl\":\"/media/maslenica.jpg\",\n" +
//                "\t\"lead\":\"Празничные гуляния на площади\",\n" +
//                "\t\"description\":\"Описание масленичных гуляний\",\n" +
//                "\t\"ageLimit\":\"0\",\n" +
//                "\t\"timing\":\"180\",\n" +
//                "\t\"place\":\"Центральная площадь, г.Белгород\",\n" +
//                "\t\"phone\":\"89202005544\",\n" +
//                "\t\"date\":1592600400,\n" +
//                "\t\"isCommercial\":false,\n" +
//                "\t\"userId\":1,\n" +
//                "\t\"sourceId\":1\n" +
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
//        String sqlQueryComment = "SELECT * FROM afisha WHERE id=1;";
//        connection = poolConnection.getConnection();
//        statement = connection.createStatement();
//        ResultSet result = statement.executeQuery(sqlQueryComment);
//        result.next();
//        soft.assertThat(afisha)
//                .hasFieldOrPropertyWithValue("title", result.getString("title"))
//                .hasFieldOrPropertyWithValue("imageUrl", result.getString("image_url"))
//                .hasFieldOrPropertyWithValue("lead", result.getString("lead"))
//                .hasFieldOrPropertyWithValue("description", result.getString("description"))
//                .hasFieldOrPropertyWithValue("ageLimit", result.getString("age_limit"))
//                .hasFieldOrPropertyWithValue("timing", result.getString("timing"))
//                .hasFieldOrPropertyWithValue("place", result.getString("place"))
//                .hasFieldOrPropertyWithValue("phone", result.getString("phone"))
//                .hasFieldOrPropertyWithValue("date", result.getTimestamp("date").toLocalDateTime().toLocalDate())
//                .hasFieldOrPropertyWithValue("isCommercial", result.getBoolean("is_commercial"))
//                .hasFieldOrPropertyWithValue("userId", result.getInt("user_id"))
//                .hasFieldOrPropertyWithValue("sourceId", result.getInt("source_id"));
//        soft.assertAll();
//    }

//    @Test
//    void buildResponseDELETEMethod() throws SQLException, IOException {
//        Connection connection = this.poolConnection.getConnection();
//        Statement statement = connection.createStatement();
//        String sqlInsertAfisha = String.format("INSERT INTO afisha (title, image_url, lead, description, age_limit, " +
//                        "timing, place, phone, date, is_commercial, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', " +
//                        "'%s', '%s', '%s', '%s', %s, %s, %s);", "title1", "image_url1", "lead1", "description1", "age1",
//                "timing1", "place1", "phone1", Timestamp.valueOf(date.atStartOfDay()), true, 1, 1);
//        statement.executeUpdate(sqlInsertAfisha);
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
//                "DELETE /afisha/1/ HTTP/1.1\n" +
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
//        String sqlQueryAfisha = "SELECT * FROM afisha WHERE id=1;";
//        ResultSet result = statement.executeQuery(sqlQueryAfisha);
//        assertThat(result.next()).isFalse().as("Не удалена афиша по запросу");
//    }
}