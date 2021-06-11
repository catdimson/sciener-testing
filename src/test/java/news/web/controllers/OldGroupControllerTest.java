package news.web.controllers;

import news.dao.connection.DBPool;
import news.model.Group;
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

class OldGroupControllerTest {
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

        String sqlCreateTableGroup = "CREATE TABLE IF NOT EXISTS \"group\" (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "title character varying(40) NOT NULL," +
                "CONSTRAINT group_pk PRIMARY KEY (id)," +
                "CONSTRAINT title_unique UNIQUE (title)" +
                ");";
        this.poolConnection = new DBPool(this.container.getJdbcUrl(), this.container.getUsername(), this.container.getPassword());

        Statement statement = this.poolConnection.getConnection().createStatement();
        statement.executeUpdate(sqlCreateTableGroup);
    }

    @Test
    void buildResponseGETMethodFindAll() throws IOException, SQLException {
        Connection connection = this.poolConnection.getConnection();
        Statement statement = connection.createStatement();
        String sqlInsertGroup = "INSERT INTO \"group\" (title) VALUES ('Редактор'), ('Админушка');";
        statement.executeUpdate(sqlInsertGroup);

        // ожидаемый результат
        String expectedResult = "" +
            "HTTP/1.1 200 \n" +
            "Cache-Control: no-store, no-cache, must-revalidate\n" +
            "Pragma: no-cache\n" +
            "Content-Type: application/json;charset=UTF-8\n" +
            "Content-Length: 93\n" +
            "\n" +
            "[\n" +
            "{\n" +
            "\t\"id\": 1,\n" +
            "\t\"title\": \"Редактор\"\n" +
            "},\n" +
            "{\n" +
            "\t\"id\": 2,\n" +
            "\t\"title\": \"Админушка\"\n" +
            "}\n" +
            "]";
        clientSocket = new Socket("127.0.0.1", 8080);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));

        String request = "" +
                "GET /blg_kotik_dmitry_war/group/ HTTP/1.1\n" +
                "Accept: application/json, */*; q=0.01\n" +
                "Content-Type: application/json\n" +
                "Host: 127.0.0.1:8080\n" +
                "UnitTest: true\n" +
                "UrlPostgres: " + this.container.getJdbcUrl() + "\n" +
                "UserPostgres: " + this.container.getUsername() + "\n" +
                "PasswordPostgres: " + this.container.getPassword() + "\n";
        out.println(request);
        out.flush();

        StringBuilder actualResult = new StringBuilder();
        actualResult.append(in.readLine()).append("\n");
        while (in.ready()) {
            String line = in.readLine();
            if (line.contains("Date: ")) {
                continue;
            }
            actualResult.append(line).append("\n");
        }
        actualResult.setLength(actualResult.length() - 1);
        assertThat(actualResult.toString()).isEqualTo(expectedResult);
   }

    @Test
    void buildResponseGETMethodFindByTitle() throws IOException, SQLException {
        Connection connection = this.poolConnection.getConnection();
        Statement statement = connection.createStatement();
        String sqlInsertGroup = "INSERT INTO \"group\" (title) VALUES ('editor'), ('Админушка');";
        statement.executeUpdate(sqlInsertGroup);

        // ожидаемый результат
        String expectedResult = "" +
                "HTTP/1.1 200 \n" +
                "Cache-Control: no-store, no-cache, must-revalidate\n" +
                "Pragma: no-cache\n" +
                "Content-Type: application/json;charset=UTF-8\n" +
                "Content-Length: 37\n" +
                "\n" +
                "[\n" +
                "{\n" +
                "\t\"id\": 1,\n" +
                "\t\"title\": \"editor\"\n" +
                "}\n" +
                "]";
        clientSocket = new Socket("127.0.0.1", 8080);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));

        String request = "" +
                "GET /blg_kotik_dmitry_war/group?title=editor HTTP/1.1\n" +
                "Accept: application/json, */*; q=0.01\n" +
                "Content-Type: application/json\n" +
                "Host: 127.0.0.1:8080\n" +
                "UnitTest: true\n" +
                "UrlPostgres: " + this.container.getJdbcUrl() + "\n" +
                "UserPostgres: " + this.container.getUsername() + "\n" +
                "PasswordPostgres: " + this.container.getPassword() + "\n";
        out.println(request);
        out.flush();

        StringBuilder actualResult = new StringBuilder();
        actualResult.append(in.readLine()).append("\n");
        while (in.ready()) {
            String line = in.readLine();
            if (line.contains("Date: ")) {
                continue;
            }
            actualResult.append(line).append("\n");
        }
        actualResult.setLength(actualResult.length() - 1);
        assertThat(actualResult.toString()).isEqualTo(expectedResult);
    }

    @Test
    void buildResponseGETMethodFindById() throws SQLException, IOException {
        Connection connection = this.poolConnection.getConnection();
        Statement statement = connection.createStatement();
        String sqlInsertGroup = "INSERT INTO \"group\" (title) VALUES ('Редактор'), ('Админушка');";
        statement.executeUpdate(sqlInsertGroup);

        // ожидаемый результат
        String expectedResult = "" +
                "HTTP/1.1 200 \n" +
                "Cache-Control: no-store, no-cache, must-revalidate\n" +
                "Pragma: no-cache\n" +
                "Content-Type: application/json;charset=UTF-8\n" +
                "Content-Length: 42\n" +
                "\n" +
                "{\n" +
                "\t\"id\": 1,\n" +
                "\t\"title\": \"Редактор\"\n" +
                "}";

        clientSocket = new Socket("127.0.0.1", 8080);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));

        String request = "" +
                "GET /blg_kotik_dmitry_war/group/1/ HTTP/1.1\n" +
                "Accept: application/json, */*; q=0.01\n" +
                "Content-Type: application/json\n" +
                "Host: 127.0.0.1:8080\n" +
                "UnitTest: true\n" +
                "UrlPostgres: " + this.container.getJdbcUrl() + "\n" +
                "UserPostgres: " + this.container.getUsername() + "\n" +
                "PasswordPostgres: " + this.container.getPassword() + "\n";
        out.println(request);
        out.flush();

        StringBuilder actualResult = new StringBuilder();
        actualResult.append(in.readLine()).append("\n");
        while (in.ready()) {
            String line = in.readLine();
            if (line.contains("Date: ")) {
                continue;
            }
            actualResult.append(line).append("\n");
        }
        actualResult.setLength(actualResult.length() - 1);
        assertThat(actualResult.toString()).isEqualTo(expectedResult);
    }

    @Test
    void buildResponsePOSTMethod() throws SQLException, IOException {
        Group group = new Group("Пользователь");

        clientSocket = new Socket("127.0.0.1", 8080);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));
        String expectedResult = "" +
            "HTTP/1.1 201 \n" +
            "Cache-Control: no-store, no-cache, must-revalidate\n" +
            "Pragma: no-cache\n" +
            "Location: /group/1/\n" +
            "Content-Length: 0\n";

        String request = "" +
            "POST /blg_kotik_dmitry_war/group/ HTTP/1.1\n" +
            "Accept: application/json, */*; q=0.01\n" +
            "Content-Type: application/json\n" +
            "Content-length: 100\n" +
            "Host: 127.0.0.1:8080\n" +
            "UnitTest: true\n" +
            "UrlPostgres: " + this.container.getJdbcUrl() + "\n" +
            "UserPostgres: " + this.container.getUsername() + "\n" +
            "PasswordPostgres: " + this.container.getPassword() + "\n" +
            "\n" +
            "{\n" +
            "\t\"title\": \"Пользователь\",\n" +
            "}\n";
        out.println(request);
        out.flush();

        StringBuilder actualResult = new StringBuilder();
        actualResult.append(in.readLine()).append("\n");
        while (in.ready()) {
            String line = in.readLine();
            if (line.contains("Date: ")) {
                continue;
            }
            actualResult.append(line).append("\n");
        }
        actualResult.setLength(actualResult.length() - 1);
        // сначала сравниваем ответы
        assertThat(actualResult.toString()).isEqualTo(expectedResult);
        // сравниваем результаты
        String sqlQueryGroup = "SELECT * FROM \"group\" WHERE id=1;";
        Connection connection = poolConnection.getConnection();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sqlQueryGroup);
        result.next();
        assertThat(group).hasFieldOrPropertyWithValue("title", result.getString("title"));
    }

    @Test
    void buildResponsePUTMethod() throws IOException, SQLException {
        SoftAssertions soft = new SoftAssertions();
        Connection connection = this.poolConnection.getConnection();
        Statement statement = connection.createStatement();
        String sqlInsertGroup = "INSERT INTO \"group\" (title) VALUES ('Админ');";
        statement.executeUpdate(sqlInsertGroup);
        Group group = new Group("Редактор");

        clientSocket = new Socket("127.0.0.1", 8080);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));
        String expectedResult = "" +
                "HTTP/1.1 204 \n" +
                "Cache-Control: no-store, no-cache, must-revalidate\n" +
                "Pragma: no-cache\n";

        String request = "" +
                "PUT /blg_kotik_dmitry_war/group/1/ HTTP/1.1\n" +
                "Accept: application/json, */*; q=0.01\n" +
                "Content-Type: application/json\n" +
                "Content-length: 100\n" +
                "Host: 127.0.0.1:8080\n" +
                "UnitTest: true\n" +
                "UrlPostgres: " + this.container.getJdbcUrl() + "\n" +
                "UserPostgres: " + this.container.getUsername() + "\n" +
                "PasswordPostgres: " + this.container.getPassword() + "\n" +
                "\n" +
                "{\n" +
                "\t\"id\": 1,\n" +
                "\t\"title\": \"Редактор\"\n" +
                "}";
        out.println(request);
        out.flush();

        StringBuilder actualResult = new StringBuilder();
        actualResult.append(in.readLine()).append("\n");
        while (in.ready()) {
            String line = in.readLine();
            if (line.contains("Date: ")) {
                continue;
            }
            actualResult.append(line).append("\n");
        }
        actualResult.setLength(actualResult.length() - 1);
        // сначала сравниваем ответы
        assertThat(actualResult.toString()).isEqualTo(expectedResult);
        // сравниваем результаты
        String sqlQueryGroup = "SELECT * FROM \"group\" WHERE id=1;";
        connection = poolConnection.getConnection();
        statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sqlQueryGroup);
        result.next();
        soft.assertThat(group).hasFieldOrPropertyWithValue("title", result.getString("title"));;
    }

    @Test
    void buildResponseDELETEMethod() throws SQLException, IOException {
        Connection connection = this.poolConnection.getConnection();
        Statement statement = connection.createStatement();
        String sqlInsertGroup = "INSERT INTO \"group\" (title) VALUES ('Админ');";
        statement.executeUpdate(sqlInsertGroup);

        clientSocket = new Socket("127.0.0.1", 8080);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));
        String expectedResult = "" +
                "HTTP/1.1 204 \n" +
                "Cache-Control: no-store, no-cache, must-revalidate\n" +
                "Pragma: no-cache\n";

        String request = "" +
                "DELETE /blg_kotik_dmitry_war/group/1/ HTTP/1.1\n" +
                "Accept: application/json, */*; q=0.01\n" +
                "Content-Type: application/json\n" +
                "Host: 127.0.0.1:8080\n" +
                "UnitTest: true\n" +
                "UrlPostgres: " + this.container.getJdbcUrl() + "\n" +
                "UserPostgres: " + this.container.getUsername() + "\n" +
                "PasswordPostgres: " + this.container.getPassword() + "\n";
        out.println(request);
        out.flush();

        StringBuilder actualResult = new StringBuilder();
        actualResult.append(in.readLine()).append("\n");
        while (in.ready()) {
            String line = in.readLine();
            if (line.contains("Date: ")) {
                continue;
            }
            actualResult.append(line).append("\n");
        }
        actualResult.setLength(actualResult.length() - 1);
        // сначала сравниваем ответы
        assertThat(actualResult.toString()).isEqualTo(expectedResult);
        // сравниваем результаты из таблиц
        String sqlQueryGroup = "SELECT * FROM \"group\" WHERE id=1;";
        ResultSet result = statement.executeQuery(sqlQueryGroup);
        assertThat(result.next()).isFalse().as("Не удалена группа по запросу");
    }
}