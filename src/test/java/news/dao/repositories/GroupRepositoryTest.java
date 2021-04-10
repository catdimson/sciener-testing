package news.dao.repositories;

import news.dao.connection.DBPool;
import news.dao.specifications.FindByIdGroupSpecification;
import news.dao.specifications.FindByTitleGroupSpecification;
import news.model.Group;
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

class GroupRepositoryTest {
    private PostgreSQLContainer container;
    private DBPool poolConnection;

    @BeforeEach
    void setUp() throws SQLException {
        this.container = new PostgreSQLContainer("postgres")
                .withUsername("admin")
                .withPassword("qwerty")
                .withDatabaseName("news");
        this.container.start();

        String sqlCreateTableCategory = "CREATE TABLE IF NOT EXISTS \"group\" (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "title character varying(40) NOT NULL," +
                "CONSTRAINT group_pk PRIMARY KEY (id)," +
                "CONSTRAINT title_unique UNIQUE (title)" +
                ");";
        this.poolConnection = new DBPool(this.container.getJdbcUrl(), this.container.getUsername(), this.container.getPassword());

        Statement statement = this.poolConnection.getConnection().createStatement();
        statement.executeUpdate(sqlCreateTableCategory);
    }

    @Test
    void findById() {
        try {
            SoftAssertions soft = new SoftAssertions();
            GroupRepository groupRepository = new GroupRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlInsertInstance = "INSERT INTO \"group\" (title) VALUES('Редактор');";
            statement.executeUpdate(sqlInsertInstance);
            Group group = new Group(1,"Редактор");

            FindByIdGroupSpecification findById = new FindByIdGroupSpecification(1);
            List<Group> resultFindByIdGroup = groupRepository.query(findById);

            soft.assertThat(group)
                    .hasFieldOrPropertyWithValue("id", resultFindByIdGroup.get(0).getObjects()[0])
                    .hasFieldOrPropertyWithValue("title", resultFindByIdGroup.get(0).getObjects()[1]);
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
            GroupRepository groupRepository = new GroupRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlInsertInstance = "INSERT INTO \"group\" (title) VALUES('Редактор');";
            statement.executeUpdate(sqlInsertInstance);
            Group group = new Group(1,"Редактор");

            FindByTitleGroupSpecification findByTitle = new FindByTitleGroupSpecification("Редактор");
            List<Group> resultFindByIdGroup = groupRepository.query(findByTitle);

            soft.assertThat(group)
                    .hasFieldOrPropertyWithValue("id", resultFindByIdGroup.get(0).getObjects()[0])
                    .hasFieldOrPropertyWithValue("title", resultFindByIdGroup.get(0).getObjects()[1]);
            soft.assertAll();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void createCategory() {
        try {
            GroupRepository groupRepository = new GroupRepository(this.poolConnection);
            Group group = new Group("Редактор");

            groupRepository.create(group);

            Connection connection = this.poolConnection.getConnection();
            String sqlQueryInstanceFromTableGroup = "SELECT id, title FROM \"group\" WHERE title='Редактор'";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sqlQueryInstanceFromTableGroup);
            result.next();
            assertThat(group).hasFieldOrPropertyWithValue("title", result.getString(2));
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void deleteCategory() {
        try {
            GroupRepository groupRepository = new GroupRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlInsertInstance = "INSERT INTO \"group\" (title) VALUES('Редактор');";
            statement.executeUpdate(sqlInsertInstance, Statement.RETURN_GENERATED_KEYS);
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();

            groupRepository.delete(generatedKeys.getInt(1));

            String sqlQueryInstance = String.format("SELECT id, title FROM \"group\" WHERE id=%d;", generatedKeys.getInt(1));
            ResultSet result = statement.executeQuery(sqlQueryInstance);
            assertThat(result.next()).as("Запись класса Group не была удалена").isFalse();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void updateCategory() {
        try {
            GroupRepository groupRepository = new GroupRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlInsertInstance = "INSERT INTO \"group\" (title) VALUES('Редактор');";
            statement.executeUpdate(sqlInsertInstance);
            Group group = new Group(1, "Администратор");
            Object[] instance = group.getObjects();

            groupRepository.update(group);

            String sqlQueryInstance = String.format("SELECT id, title FROM \"group\" WHERE id=%s;", instance[0]);
            ResultSet result = statement.executeQuery(sqlQueryInstance);
            result.next();
            assertThat(group).hasFieldOrPropertyWithValue("title", result.getString(2));
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}