package news.dao.repositories;

import news.HibernateUtil;
import news.dao.connection.DBPool;
import news.dao.specifications.FindAllSourceSpecification;
import news.dao.specifications.FindByIdSourceSpecification;
import news.dao.specifications.FindByTitleSourceSpecification;
import news.model.Source;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SourceRepositoryTest {
    private PostgreSQLContainer container;
    private DBPool poolConnection;

    @BeforeEach
    void setUp() throws SQLException {
        this.container = new PostgreSQLContainer("postgres")
                .withUsername("admin")
                .withPassword("qwerty")
                .withDatabaseName("news");
        this.container.start();
        
        this.poolConnection = new DBPool(this.container.getJdbcUrl(), this.container.getUsername(), this.container.getPassword());
        
        HibernateUtil.setConnectionProperties(this.container.getJdbcUrl(), this.container.getUsername(), this.container.getPassword());

        Statement statement = this.poolConnection.getConnection().createStatement();
        
        String sqlCreateTableSource = "CREATE TABLE IF NOT EXISTS source (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "title character varying(50) NOT NULL," +
                "url character varying(500) NOT NULL," +
                "CONSTRAINT source_pk PRIMARY KEY (id)" +
                ");";
        statement.executeUpdate(sqlCreateTableSource);
    }

    @Test
    void findById() {
        try {
            SoftAssertions soft = new SoftAssertions();
            SourceRepository sourceRepository = new SourceRepository();
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlInsertInstance = "INSERT INTO source (title, url) VALUES('Яндекс ДЗЕН', 'https://zen.yandex.ru/');";
            statement.executeUpdate(sqlInsertInstance);
            Source source = new Source("Яндекс ДЗЕН","https://zen.yandex.ru/");

            FindByIdSourceSpecification findById = new FindByIdSourceSpecification(1);
            List<Source> resultFindByIdSource = sourceRepository.query(findById);

            soft.assertThat(source)
                    .hasFieldOrPropertyWithValue("title", resultFindByIdSource.get(0).getObjects()[1])
                    .hasFieldOrPropertyWithValue("url", resultFindByIdSource.get(0).getObjects()[2]);
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
            SourceRepository sourceRepository = new SourceRepository();
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlInsertInstance = "INSERT INTO source (title, url) VALUES('Яндекс ДЗЕН', 'https://zen.yandex.ru/');";
            statement.executeUpdate(sqlInsertInstance);
            sqlInsertInstance = "INSERT INTO source (title, url) VALUES('Яндекс ДЗЕН', 'https://zen.yandex.ru/1234');";
            statement.executeUpdate(sqlInsertInstance);
            Source source1 = new Source("Яндекс ДЗЕН", "https://zen.yandex.ru/");
            Source source2 = new Source("Яндекс ДЗЕН", "https://zen.yandex.ru/1234");

            FindByTitleSourceSpecification findByTitle = new FindByTitleSourceSpecification("Яндекс ДЗЕН");
            List<Source> resultFindByIdSource = sourceRepository.query(findByTitle);

            soft.assertThat(source1)
                    .hasFieldOrPropertyWithValue("title", resultFindByIdSource.get(0).getObjects()[1])
                    .hasFieldOrPropertyWithValue("url", resultFindByIdSource.get(0).getObjects()[2]);
            soft.assertAll();
            soft.assertThat(source2)
                    .hasFieldOrPropertyWithValue("title", resultFindByIdSource.get(1).getObjects()[1])
                    .hasFieldOrPropertyWithValue("url", resultFindByIdSource.get(1).getObjects()[2]);
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
            SourceRepository sourceRepository = new SourceRepository();
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlInsertInstance = "INSERT INTO source (title, url) VALUES('Яндекс ДЗЕН', 'https://zen.yandex.ru/'), " +
                    "('РИА Новости', 'https://ria.ru/'), ('Яндекс ДЗЕН', 'https://zen.yandex.ru/1234');";
            statement.executeUpdate(sqlInsertInstance);
            Source source1 = new Source("Яндекс ДЗЕН", "https://zen.yandex.ru/");
            Source source2 = new Source("РИА Новости", "https://ria.ru/");
            Source source3 = new Source("Яндекс ДЗЕН", "https://zen.yandex.ru/1234");

            FindAllSourceSpecification findAll = new FindAllSourceSpecification();
            List<Source> resultFindAllSource = sourceRepository.query(findAll);

            soft.assertThat(source1)
                    .hasFieldOrPropertyWithValue("title", resultFindAllSource.get(0).getObjects()[1])
                    .hasFieldOrPropertyWithValue("url", resultFindAllSource.get(0).getObjects()[2]);
            soft.assertAll();
            soft.assertThat(source2)
                    .hasFieldOrPropertyWithValue("title", resultFindAllSource.get(1).getObjects()[1])
                    .hasFieldOrPropertyWithValue("url", resultFindAllSource.get(1).getObjects()[2]);
            soft.assertAll();
            soft.assertThat(source3)
                    .hasFieldOrPropertyWithValue("title", resultFindAllSource.get(2).getObjects()[1])
                    .hasFieldOrPropertyWithValue("url", resultFindAllSource.get(2).getObjects()[2]);
            soft.assertAll();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void createSource() {
        try {
            SoftAssertions soft = new SoftAssertions();
            SourceRepository sourceRepository = new SourceRepository();
            Source source = new Source("Яндекс ДЗЕН", "https://zen.yandex.ru/");

            sourceRepository.create(source);

            Connection connection = this.poolConnection.getConnection();
            String sqlQueryInstanceFromTableSource = "SELECT * FROM source WHERE title='Яндекс ДЗЕН' AND url='https://zen.yandex.ru/'";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sqlQueryInstanceFromTableSource);
            result.next();
            soft.assertThat(source)
                    .hasFieldOrPropertyWithValue("title", result.getString(2))
                    .hasFieldOrPropertyWithValue("url", result.getString(3));
            soft.assertAll();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void deleteSource() {
        try {
            SourceRepository sourceRepository = new SourceRepository();
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlInsertInstance = "INSERT INTO source (title, url) VALUES('Яндекс ДЗЕН', 'https://zen.yandex.ru/');";
            statement.executeUpdate(sqlInsertInstance, Statement.RETURN_GENERATED_KEYS);
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();

            sourceRepository.delete(generatedKeys.getInt(1));

            String sqlQueryInstance = String.format("SELECT * FROM source WHERE id=%d;", generatedKeys.getInt(1));
            ResultSet result = statement.executeQuery(sqlQueryInstance);
            assertThat(result.next()).as("Запись класса Source не была удалена").isFalse();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void updateSource() {
        try {
            SoftAssertions soft = new SoftAssertions();
            SourceRepository sourceRepository = new SourceRepository();
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlInsertInstance = "INSERT INTO source (title, url) VALUES('Яндекс ДЗЕН', 'https://zen.yandex.ru/');";
            statement.executeUpdate(sqlInsertInstance);
            Source source = new Source(1,"РИА", "https://ria.ru/");
            Object[] instance = source.getObjects();

            sourceRepository.update(source);

            String sqlQueryInstance = String.format("SELECT * FROM source WHERE id=%s;", instance[0]);
            ResultSet result = statement.executeQuery(sqlQueryInstance);
            result.next();
            soft.assertThat(source)
                    .hasFieldOrPropertyWithValue("title", result.getString(2))
                    .hasFieldOrPropertyWithValue("url", result.getString(3));
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}