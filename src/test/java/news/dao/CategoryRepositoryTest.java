package news.dao;

import news.model.Category;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertFalse;

class CategoryRepositoryTest {
    private PostgreSQLContainer container;
    private DBPool poolConnection;

    @BeforeEach
    void setUp() throws SQLException {
        this.container = new PostgreSQLContainer("postgres")
                .withUsername("admin")
                .withPassword("qwerty")
                .withDatabaseName("news");
        this.container.start();

        String sqlCreateTableCategory = "CREATE TABLE IF NOT EXISTS category (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ), " +
                "title character varying(50) NOT NULL, " +
                "CONSTRAINT category_pk PRIMARY KEY (id)," +
                "CONSTRAINT title_unique UNIQUE (title));";
        this.poolConnection = new DBPool(this.container.getJdbcUrl(), this.container.getUsername(), this.container.getPassword());

        Statement statement = this.poolConnection.getConnection().createStatement();
        statement.executeUpdate(sqlCreateTableCategory);
    }

    @Test
    void createCategory() throws SQLException {
        SoftAssertions soft = new SoftAssertions();
        CategoryRepository categoryRepository = new CategoryRepository(this.poolConnection);
        Category category = new Category(1, "Новости");

        categoryRepository.create(category);

        Connection connection = this.poolConnection.getConnection();
        String sqlQueryInstanceFromTableCategory = "SELECT id, title FROM category WHERE title='Новости'";
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sqlQueryInstanceFromTableCategory);
        result.next();
        soft.assertThat(category)
                .hasFieldOrPropertyWithValue("id", result.getInt(1))
                .hasFieldOrPropertyWithValue("title", result.getString(2));
        soft.assertAll();
    }

    @Test
    void deleteCategory() throws SQLException {
        CategoryRepository categoryRepository = new CategoryRepository(this.poolConnection);
        Connection connection = this.poolConnection.getConnection();
        Statement statement = connection.createStatement();
        String sqlInsertInstance = "INSERT INTO category (title) VALUES('Новости');";
        statement.executeUpdate(sqlInsertInstance, Statement.RETURN_GENERATED_KEYS);
        ResultSet generatedKeys = statement.getGeneratedKeys();
        generatedKeys.next();

        categoryRepository.delete(generatedKeys.getInt(1));

        String sqlQueryInstance = String.format("SELECT id, title FROM category WHERE id=%d;",
                generatedKeys.getInt(1));
        ResultSet result = statement.executeQuery(sqlQueryInstance);
        assertFalse(result.next());
    }

    @Test
    void updateCategory() throws SQLException {
//        String jdbcUrl = container.getJdbcUrl();
    }
}