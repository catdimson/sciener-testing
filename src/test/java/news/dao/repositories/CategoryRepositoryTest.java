package news.dao.repositories;

import news.dao.connection.DBPool;
import news.dao.specifications.FindByIdCategorySpecification;
import news.dao.specifications.FindByTitleCategorySpecification;
import news.model.Category;
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
                "CONSTRAINT title_unique_category UNIQUE (title));";
        this.poolConnection = new DBPool(this.container.getJdbcUrl(), this.container.getUsername(), this.container.getPassword());

        Statement statement = this.poolConnection.getConnection().createStatement();
        statement.executeUpdate(sqlCreateTableCategory);
    }

    @Test
    void findById() {
        try {
            SoftAssertions soft = new SoftAssertions();
            CategoryRepository categoryRepository = new CategoryRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlInsertInstance = "INSERT INTO category (title) VALUES('Новости');";
            statement.executeUpdate(sqlInsertInstance);
            Category category = new Category(1,"Новости");

            FindByIdCategorySpecification findById = new FindByIdCategorySpecification(1);
            List<Category> resultFindByIdCategory = categoryRepository.query(findById);

            soft.assertThat(category)
                    .hasFieldOrPropertyWithValue("id", resultFindByIdCategory.get(0).getObjects()[0])
                    .hasFieldOrPropertyWithValue("title", resultFindByIdCategory.get(0).getObjects()[1]);
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
            CategoryRepository categoryRepository = new CategoryRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlInsertInstance = "INSERT INTO category (title) VALUES('Новости');";
            statement.executeUpdate(sqlInsertInstance);
            Category category = new Category(1,"Новости");

            FindByTitleCategorySpecification findByTitle = new FindByTitleCategorySpecification("Новости");
            List<Category> resultFindByIdCategory = categoryRepository.query(findByTitle);

            soft.assertThat(category)
                    .hasFieldOrPropertyWithValue("id", resultFindByIdCategory.get(0).getObjects()[0])
                    .hasFieldOrPropertyWithValue("title", resultFindByIdCategory.get(0).getObjects()[1]);
            soft.assertAll();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void createCategory() {
        try {
            CategoryRepository categoryRepository = new CategoryRepository(this.poolConnection);
            Category category = new Category("Новости");

            categoryRepository.create(category);

            Connection connection = this.poolConnection.getConnection();
            String sqlQueryInstanceFromTableCategory = "SELECT id, title FROM category WHERE title='Новости'";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sqlQueryInstanceFromTableCategory);
            result.next();
            assertThat(category).hasFieldOrPropertyWithValue("title", result.getString(2));
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void deleteCategory() {
        try {
            CategoryRepository categoryRepository = new CategoryRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlInsertInstance = "INSERT INTO category (title) VALUES('Новости');";
            statement.executeUpdate(sqlInsertInstance, Statement.RETURN_GENERATED_KEYS);
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();

            categoryRepository.delete(generatedKeys.getInt(1));

            String sqlQueryInstance = String.format("SELECT id, title FROM category WHERE id=%d;", generatedKeys.getInt(1));
            ResultSet result = statement.executeQuery(sqlQueryInstance);
            assertThat(result.next()).as("Запись класса Category не была удалена").isFalse();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void updateCategory() {
        try {
            CategoryRepository categoryRepository = new CategoryRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlInsertInstance = "INSERT INTO category (title) VALUES('Новости');";
            statement.executeUpdate(sqlInsertInstance);
            Category category = new Category(1, "Политика");
            Object[] instance = category.getObjects();

            categoryRepository.update(category);

            String sqlQueryInstance = String.format("SELECT id, title FROM category WHERE id=%s;", instance[0]);
            ResultSet result = statement.executeQuery(sqlQueryInstance);
            result.next();
            assertThat(category).hasFieldOrPropertyWithValue("title", result.getString(2));
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}