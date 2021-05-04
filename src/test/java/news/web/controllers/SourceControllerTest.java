package news.web.controllers;

import news.dao.connection.DBPool;
import news.model.Source;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

class SourceControllerTest {
    private PostgreSQLContainer container;
    private DBPool poolConnection;
    // для клиента
    private static Socket clientSocket;
    private static BufferedReader in;
    private static PrintWriter out;

    @BeforeEach
    void setUp() throws SQLException {
        this.container = new PostgreSQLContainer("postgres")
                .withUsername("admin")
                .withPassword("qwerty")
                .withDatabaseName("news");
        this.container.start();

        String sqlCreateTableSource = "CREATE TABLE IF NOT EXISTS source (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "title character varying(50) NOT NULL," +
                "url character varying(500) NOT NULL," +
                "CONSTRAINT source_pk PRIMARY KEY (id)" +
                ");";
        this.poolConnection = new DBPool(this.container.getJdbcUrl(), this.container.getUsername(), this.container.getPassword());

        Statement statement = this.poolConnection.getConnection().createStatement();
        statement.executeUpdate(sqlCreateTableSource);
    }

    @Test
    void buildResponseGETMethodFindAll() throws IOException, SQLException {
        Connection connection = this.poolConnection.getConnection();
        Statement statement = connection.createStatement();
        String sqlInsertSource = "INSERT INTO source (title, url) " +
                "VALUES ('Яндекс ДЗЕН', 'https://zen.yandex.ru/'), ('РИА', 'https://ria.ru/');";
        statement.executeUpdate(sqlInsertSource);

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
            "\t\"title\":\"Яндекс ДЗЕН\",\n" +
            "\t\"url\":\"https://zen.yandex.ru/\"\n" +
            "},\n" +
            "{\n" +
            "\t\"id\":2,\n" +
            "\t\"title\":\"РИА\",\n" +
            "\t\"url\":\"https://ria.ru/\"\n" +
            "}\n" +
            "]\n";
        clientSocket = new Socket("127.0.0.1", 5000);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));

        String request = "" +
                "GET /source/ HTTP/1.1\n" +
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
        String sqlInsertSource = "INSERT INTO source (title, url) " +
                "VALUES ('source1', 'https://zen.yandex.ru/'), ('source1', 'https://ria.ru/');";
        statement.executeUpdate(sqlInsertSource);

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
                "\t\"title\":\"source1\",\n" +
                "\t\"url\":\"https://zen.yandex.ru/\"\n" +
                "},\n" +
                "{\n" +
                "\t\"id\":2,\n" +
                "\t\"title\":\"source1\",\n" +
                "\t\"url\":\"https://ria.ru/\"\n" +
                "}\n" +
                "]\n";
        clientSocket = new Socket("127.0.0.1", 5000);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));

        String request = "" +
                "GET /source?title=source1 HTTP/1.1\n" +
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
        String sqlInsertSource = "INSERT INTO source (title, url) " +
                "VALUES ('source1', 'https://zen.yandex.ru/');";
        statement.executeUpdate(sqlInsertSource);

        // ожидаемый результат
        String expectedResult = "" +
                "HTTP/1.1 200 OK\n" +
                "Cache-Control: no-store, no-cache, must-revalidate\n" +
                "Pragma: no-cache\n" +
                "Content-Type: application/json; charset=UTF-8\n" +
                "\n" +
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"title\":\"source1\",\n" +
                "\t\"url\":\"https://zen.yandex.ru/\"\n" +
                "}";

        clientSocket = new Socket("127.0.0.1", 5000);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));

        String request = "" +
                "GET /source/1/ HTTP/1.1\n" +
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
        Source source = new Source("Яндекс ДЗЕН","https://zen.yandex.ru/");

        clientSocket = new Socket("127.0.0.1", 5000);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));
        String expectedResult = "" +
            "HTTP/1.1 201 Источник создан\n" +
            "Cache-Control: no-store, no-cache, must-revalidate\n" +
            "Pragma: no-cache\n" +
            "Location: /source/1/\n";

        String request = "" +
            "POST /source/ HTTP/1.1\n" +
            "Accept: application/json, */*; q=0.01\n" +
            "Content-Type: application/json\n" +
            "Host: 127.0.0.1:5000\n" +
            "UnitTest: true\n" +
            "UrlPostgres: " + this.container.getJdbcUrl() + "\n" +
            "UserPostgres: " + this.container.getUsername() + "\n" +
            "PasswordPostgres: " + this.container.getPassword() + "\n" +
            "\n" +
            "{\n" +
            "\t\"title\":\"Яндекс ДЗЕН\",\n" +
            "\t\"url\":\"https://zen.yandex.ru/\",\n" +
            "}\n";
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
        String sqlQuerySource = "SELECT * FROM source WHERE id=1;";
        Connection connection = poolConnection.getConnection();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sqlQuerySource);
        result.next();
        soft.assertThat(source)
                .hasFieldOrPropertyWithValue("title", result.getString("title"))
                .hasFieldOrPropertyWithValue("url", result.getString("url"));
        soft.assertAll();
    }

//    @Test
//    void buildResponsePUTMethod() throws IOException, SQLException {
//        SoftAssertions soft = new SoftAssertions();
//        Connection connection = this.poolConnection.getConnection();
//        Statement statement = connection.createStatement();
//        String sqlInsertUser = String.format("INSERT INTO \"user\" (password, username, first_name, last_name, email, " +
//                        "last_login, date_joined, is_superuser, is_staff, is_active, group_id) VALUES ('%s', '%s', '%s', '%s', " +
//                        "'%s', '%s', '%s', %s, %s, %s, %s);", "password1", "username1", "firstname1", "lastname1", "email1@mail.ru",
//                Timestamp.valueOf(lastLogin.atStartOfDay()), Timestamp.valueOf(dateJoined.atStartOfDay()), true, true, true, 1);
//        statement.executeUpdate(sqlInsertUser);
//        User user = new User("qwerty123", "alex1992", "Александр", "Колесников",
//                "alex1993@mail.ru", lastLogin, dateJoined, true, true, true, 1);
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
//                "PUT /user/1/ HTTP/1.1\n" +
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
//                "\t\"password\":\"qwerty123\",\n" +
//                "\t\"username\":\"alex1992\",\n" +
//                "\t\"firstName\":\"Александр\",\n" +
//                "\t\"lastName\":\"Колесников\",\n" +
//                "\t\"email\":\"alex1993@mail.ru\",\n" +
//                "\t\"lastLogin\":1589922000,\n" +
//                "\t\"dateJoined\":1558299600,\n" +
//                "\t\"isSuperuser\":true,\n" +
//                "\t\"isStaff\":true,\n" +
//                "\t\"isActive\":true,\n" +
//                "\t\"groupId\":1,\n" +
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
//        String sqlQueryUser = "SELECT * FROM \"user\" WHERE id=1;";
//        connection = poolConnection.getConnection();
//        statement = connection.createStatement();
//        ResultSet result = statement.executeQuery(sqlQueryUser);
//        result.next();
//        soft.assertThat(user)
//                .hasFieldOrPropertyWithValue("password", result.getString("password"))
//                .hasFieldOrPropertyWithValue("username", result.getString("username"))
//                .hasFieldOrPropertyWithValue("firstName", result.getString("first_name"))
//                .hasFieldOrPropertyWithValue("lastName", result.getString("last_name"))
//                .hasFieldOrPropertyWithValue("email", result.getString("email"))
//                .hasFieldOrPropertyWithValue("lastLogin", result.getTimestamp("last_login").toLocalDateTime().toLocalDate())
//                .hasFieldOrPropertyWithValue("dateJoined", result.getTimestamp("date_joined").toLocalDateTime().toLocalDate())
//                .hasFieldOrPropertyWithValue("isSuperuser", result.getBoolean("is_superuser"))
//                .hasFieldOrPropertyWithValue("isStaff", result.getBoolean("is_staff"))
//                .hasFieldOrPropertyWithValue("isActive", result.getBoolean("is_active"))
//                .hasFieldOrPropertyWithValue("groupId", result.getInt("group_id"));
//        soft.assertAll();
//    }

//    @Test
//    void buildResponseDELETEMethod() throws SQLException, IOException {
//        Connection connection = this.poolConnection.getConnection();
//        Statement statement = connection.createStatement();
//        String sqlInsertUser = String.format("INSERT INTO \"user\" (password, username, first_name, last_name, email, " +
//                        "last_login, date_joined, is_superuser, is_staff, is_active, group_id) VALUES ('%s', '%s', '%s', '%s', " +
//                        "'%s', '%s', '%s', %s, %s, %s, %s);", "password1", "username1", "firstname1", "lastname1", "email1@mail.ru",
//                Timestamp.valueOf(lastLogin.atStartOfDay()), Timestamp.valueOf(dateJoined.atStartOfDay()), true, true, true, 1);
//        statement.executeUpdate(sqlInsertUser);
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
//                "DELETE /user/1/ HTTP/1.1\n" +
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
//        String sqlQueryUser = "SELECT * FROM \"user\" WHERE id=1;";
//        ResultSet result = statement.executeQuery(sqlQueryUser);
//        assertThat(result.next()).isFalse().as("Не удален пользователь по запросу");
//    }
}