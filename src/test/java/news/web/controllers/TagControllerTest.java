package news.web.controllers;

import news.dao.connection.DBPool;
import news.model.Tag;
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

class TagControllerTest {
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

        String sqlCreateTableTag = "CREATE TABLE IF NOT EXISTS tag (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "title character varying(50) NOT NULL," +
                "CONSTRAINT tag_pk PRIMARY KEY (id)" +
                ");";
        this.poolConnection = new DBPool(this.container.getJdbcUrl(), this.container.getUsername(), this.container.getPassword());

        Statement statement = this.poolConnection.getConnection().createStatement();
        statement.executeUpdate(sqlCreateTableTag);
    }

    @Test
    void buildResponseGETMethodFindAll() throws IOException, SQLException {
        Connection connection = this.poolConnection.getConnection();
        Statement statement = connection.createStatement();
        String sqlInsertTag = "INSERT INTO tag (title) VALUES ('ufc'), ('балет');";
        statement.executeUpdate(sqlInsertTag);

        // ожидаемый результат
        String expectedResult = "" +
            "HTTP/1.1 200 \n" +
            "Cache-Control: no-store, no-cache, must-revalidate\n" +
            "Pragma: no-cache\n" +
            "Content-Type: application/json;charset=UTF-8\n" +
            "Content-Length: 68\n" +
            "\n" +
            "[\n" +
            "{\n" +
            "\t\"id\":1,\n" +
            "\t\"title\":\"ufc\"\n" +
            "},\n" +
            "{\n" +
            "\t\"id\":2,\n" +
            "\t\"title\":\"балет\"\n" +
            "}\n" +
            "]";
        clientSocket = new Socket("127.0.0.1", 8080);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));

        String request = "" +
                "GET /blg_kotik_dmitry_war/tag/ HTTP/1.1\n" +
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
        String sqlInsertTag = "INSERT INTO tag (title) VALUES ('ufc'), ('балет');";
        statement.executeUpdate(sqlInsertTag);

        // ожидаемый результат
        String expectedResult = "" +
                "HTTP/1.1 200 \n" +
                "Cache-Control: no-store, no-cache, must-revalidate\n" +
                "Pragma: no-cache\n" +
                "Content-Type: application/json;charset=UTF-8\n" +
                "Content-Length: 32\n" +
                "\n" +
                "[\n" +
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"title\":\"ufc\"\n" +
                "}\n" +
                "]";
        clientSocket = new Socket("127.0.0.1", 8080);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));

        String request = "" +
                "GET /blg_kotik_dmitry_war/tag?title=ufc HTTP/1.1\n" +
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
        String sqlInsertTag = "INSERT INTO tag (title) VALUES ('ufc'), ('балет');";
        statement.executeUpdate(sqlInsertTag);

        // ожидаемый результат
        String expectedResult = "" +
                "HTTP/1.1 200 \n" +
                "Cache-Control: no-store, no-cache, must-revalidate\n" +
                "Pragma: no-cache\n" +
                "Content-Type: application/json;charset=UTF-8\n" +
                "Content-Length: 27\n" +
                "\n" +
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"title\":\"ufc\"\n" +
                "}";

        clientSocket = new Socket("127.0.0.1", 8080);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));

        String request = "" +
                "GET /blg_kotik_dmitry_war/tag/1/ HTTP/1.1\n" +
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
        Tag tag = new Tag("ufc");

        clientSocket = new Socket("127.0.0.1", 8080);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));
        String expectedResult = "" +
            "HTTP/1.1 201 \n" +
            "Cache-Control: no-store, no-cache, must-revalidate\n" +
            "Pragma: no-cache\n" +
            "Location: /tag/1/\n" +
            "Content-Length: 0\n";


        String request = "" +
            "POST /blg_kotik_dmitry_war/tag/ HTTP/1.1\n" +
            "Accept: application/json, */*; q=0.01\n" +
            "Content-Type: application/json\n" +
            "Host: 127.0.0.1:8080\n" +
            "Accept-encoding: gzip, deflate, br\n" +
            "Content-length: 38\n" +
            "UnitTest: true\n" +
            "UrlPostgres: " + this.container.getJdbcUrl() + "\n" +
            "UserPostgres: " + this.container.getUsername() + "\n" +
            "PasswordPostgres: " + this.container.getPassword() + "\n" +
            "\n" +
            "{\n" +
            "\t\"title\":\"ufc\"\n" +
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
        String sqlQueryTag = "SELECT * FROM tag WHERE id=1;";
        Connection connection = poolConnection.getConnection();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sqlQueryTag);
        result.next();
        assertThat(tag).hasFieldOrPropertyWithValue("title", result.getString("title"));
    }

    @Test
    void buildResponsePUTMethod() throws IOException, SQLException {
        SoftAssertions soft = new SoftAssertions();
        Connection connection = this.poolConnection.getConnection();
        Statement statement = connection.createStatement();
        String sqlInsertTag = "INSERT INTO tag (title) VALUES ('ufc');";
        statement.executeUpdate(sqlInsertTag);
        Tag tag = new Tag("балет");

        clientSocket = new Socket("127.0.0.1", 8080);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));
        String expectedResult = "" +
                "HTTP/1.1 204 \n" +
                "Cache-Control: no-store, no-cache, must-revalidate\n" +
                "Pragma: no-cache\n";

        String request = "" +
                "PUT /blg_kotik_dmitry_war/tag/1/ HTTP/1.1\n" +
                "Accept: application/json, */*; q=0.01\n" +
                "Content-Type: application/json\n" +
                "Accept-encoding: gzip, deflate, br\n" +
                "Content-length: 45\n" +
                "Host: 127.0.0.1:8080\n" +
                "UnitTest: true\n" +
                "UrlPostgres: " + this.container.getJdbcUrl() + "\n" +
                "UserPostgres: " + this.container.getUsername() + "\n" +
                "PasswordPostgres: " + this.container.getPassword() + "\n" +
                "\n" +
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"title\":\"балет\"\n" +
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
        String sqlQueryTag = "SELECT * FROM tag WHERE id=1;";
        connection = poolConnection.getConnection();
        statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sqlQueryTag);
        result.next();
        soft.assertThat(tag).hasFieldOrPropertyWithValue("title", result.getString("title"));;
    }

    @Test
    void buildResponseDELETEMethod() throws SQLException, IOException {
        Connection connection = this.poolConnection.getConnection();
        Statement statement = connection.createStatement();
        String sqlInsertTag= "INSERT INTO tag (title) VALUES ('ufc');";
        statement.executeUpdate(sqlInsertTag);

        clientSocket = new Socket("127.0.0.1", 8080);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(new PrintWriter(clientSocket.getOutputStream(), true));
        String expectedResult = "" +
                "HTTP/1.1 204 \n" +
                "Cache-Control: no-store, no-cache, must-revalidate\n" +
                "Pragma: no-cache\n";

        String request = "" +
                "DELETE /blg_kotik_dmitry_war/tag/1/ HTTP/1.1\n" +
                "Accept: application/json, */*; q=0.01\n" +
                "Content-Type: application/json\n" +
                "Accept-encoding: gzip, deflate, br\n" +
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
        String sqlQueryTag= "SELECT * FROM tag WHERE id=1;";
        ResultSet result = statement.executeQuery(sqlQueryTag);
        assertThat(result.next()).isFalse().as("Не удален тег по запросу");
    }
}