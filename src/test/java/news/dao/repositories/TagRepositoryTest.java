package news.dao.repositories;

import news.dao.connection.DBPool;
import news.dao.specifications.FindByIdTagSpecification;
import news.dao.specifications.FindByTitleTagSpecification;
import news.model.Tag;
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

class TagRepositoryTest {
    private PostgreSQLContainer container;
    private DBPool poolConnection;

    @BeforeEach
    void setUp() throws SQLException {
        this.container = new PostgreSQLContainer("postgres")
                .withUsername("admin")
                .withPassword("qwerty")
                .withDatabaseName("news");
        this.container.start();

        String sqlCreateTableCategory = "CREATE TABLE IF NOT EXISTS tag (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "title character varying(50) NOT NULL," +
                "CONSTRAINT tag_pk PRIMARY KEY (id)" +
                ");";
        this.poolConnection = new DBPool(this.container.getJdbcUrl(), this.container.getUsername(), this.container.getPassword());

        Statement statement = this.poolConnection.getConnection().createStatement();
        statement.executeUpdate(sqlCreateTableCategory);
    }

    @Test
    void findById() {
        try {
            SoftAssertions soft = new SoftAssertions();
            TagRepository tagRepository = new TagRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlInsertInstance = "INSERT INTO tag (title) VALUES('ufc');";
            statement.executeUpdate(sqlInsertInstance);
            Tag tag = new Tag(1,"ufc");

            FindByIdTagSpecification findById = new FindByIdTagSpecification(1);
            List<Tag> resultFindByIdTag = tagRepository.query(findById);

            soft.assertThat(tag)
                    .hasFieldOrPropertyWithValue("id", resultFindByIdTag.get(0).getObjects()[0])
                    .hasFieldOrPropertyWithValue("title", resultFindByIdTag.get(0).getObjects()[1]);
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
            TagRepository tagRepository = new TagRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlInsertInstance = "INSERT INTO tag (title) VALUES('ufc');";
            statement.executeUpdate(sqlInsertInstance);
            Tag tag = new Tag(1,"ufc");

            FindByTitleTagSpecification findByTitle = new FindByTitleTagSpecification("ufc");
            List<Tag> resultFindByIdTag = tagRepository.query(findByTitle);

            soft.assertThat(tag)
                    .hasFieldOrPropertyWithValue("id", resultFindByIdTag.get(0).getObjects()[0])
                    .hasFieldOrPropertyWithValue("title", resultFindByIdTag.get(0).getObjects()[1]);
            soft.assertAll();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void createTag() {
        try {
            TagRepository tagRepository = new TagRepository(this.poolConnection);
            Tag tag = new Tag("ufc");

            tagRepository.create(tag);

            Connection connection = this.poolConnection.getConnection();
            String sqlQueryInstanceFromTableTag = "SELECT id, title FROM tag WHERE title='ufc'";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sqlQueryInstanceFromTableTag);
            result.next();
            assertThat(tag).hasFieldOrPropertyWithValue("title", result.getString(2));
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void deleteTag() {
        try {
            TagRepository tagRepository = new TagRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlInsertInstance = "INSERT INTO tag (title) VALUES('ufc');";
            statement.executeUpdate(sqlInsertInstance, Statement.RETURN_GENERATED_KEYS);
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();

            tagRepository.delete(generatedKeys.getInt(1));

            String sqlQueryInstance = String.format("SELECT id, title FROM tag WHERE id=%d;", generatedKeys.getInt(1));
            ResultSet result = statement.executeQuery(sqlQueryInstance);
            assertThat(result.next()).as("Запись класса Tag не была удалена").isFalse();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void updateTag() {
        try {
            TagRepository tagRepository = new TagRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlInsertInstance = "INSERT INTO tag (title) VALUES('ufc');";
            statement.executeUpdate(sqlInsertInstance);
            Tag tag = new Tag(1, "балет");
            Object[] instance = tag.getObjects();

            tagRepository.update(tag);

            String sqlQueryInstance = String.format("SELECT id, title FROM tag WHERE id=%s;", instance[0]);
            ResultSet result = statement.executeQuery(sqlQueryInstance);
            result.next();
            assertThat(tag).hasFieldOrPropertyWithValue("title", result.getString(2));
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}