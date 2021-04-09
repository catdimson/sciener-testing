package news.dao;

import news.model.Category;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class CategoryRepository implements Repository<Category> {
    final private DBPool connectionPool;
    private String dbUser;
    private String dbPassword;
    private String dbConnectionUrl;

    public CategoryRepository(DBPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Category> query(Specification<Category> categorySpecification) {
        try (Connection connection = connectionPool.getConnection()) {
            return null;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        return null;
    }

    @Override
    public void create(Category category) {
        try (Connection connection = this.connectionPool.getConnection()) {
            Statement statement = connection.createStatement();
            String sqlCreateInstance = String.format("INSERT INTO category(title) VALUES('%s')", category.getTitle());
            statement.executeUpdate(sqlCreateInstance);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        try (Connection connection = this.connectionPool.getConnection()) {
            Statement statement = connection.createStatement();
            String sqlDeleteInstance = String.format("DELETE FROM category WHERE id=%d;", id);
            statement.executeUpdate(sqlDeleteInstance);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void update(Category category) {

    }
}
