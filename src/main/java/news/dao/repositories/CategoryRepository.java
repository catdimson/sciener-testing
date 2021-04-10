package news.dao.repositories;

import news.dao.connection.DBPool;
import news.dao.specifications.SqlSpecification;
import news.model.Category;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository implements Repository<Category> {
    final private DBPool connectionPool;

    public CategoryRepository(DBPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Category> query(SqlSpecification<Category> categorySpecification) throws SQLException {
        List<Category> queryResult = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        Statement statement = connection.createStatement();
        String sqlQuery = categorySpecification.toSqlClauses();
        ResultSet result = statement.executeQuery(sqlQuery);
        while (result.next()) {
            Category category = new Category(result.getInt(1), result.getString(2));
            queryResult.add(category);
        }
        return queryResult;
    }

    @Override
    public void create(Category category) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Statement statement = connection.createStatement();
        Object[] instance = category.getObjects();
        String sqlCreateInstance = String.format("INSERT INTO category(title) VALUES('%s')", instance[1]);
        statement.executeUpdate(sqlCreateInstance);
    }

    @Override
    public void delete(int id) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Statement statement = connection.createStatement();
        String sqlDeleteInstance = String.format("DELETE FROM category WHERE id=%d;", id);
        statement.executeUpdate(sqlDeleteInstance);
    }

    @Override
    public void update(Category category) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Statement statement = connection.createStatement();
        Object[] instance = category.getObjects();
        String sqlUpdateInstance = String.format("UPDATE category SET title='%s' WHERE id=%s;", instance[1], instance[0]);
        statement.executeUpdate(sqlUpdateInstance);
    }
}
