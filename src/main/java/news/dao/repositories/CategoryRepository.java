package news.dao.repositories;

import news.dao.connection.ConnectionPool;
import news.dao.connection.DBPool;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository implements ExtendRepository<Category> {
    final private ConnectionPool connectionPool;

    public CategoryRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Category> query(ExtendSqlSpecification<Category> categorySpecification) throws SQLException {
        List<Category> queryResult = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        String sqlQuery = categorySpecification.toSqlClauses();
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        if (categorySpecification.isById()) {
            preparedStatement.setInt(1, (int) categorySpecification.getCriterial());
        } else {
            if (categorySpecification.getCriterial() != null) {
                preparedStatement.setString(1, (String) categorySpecification.getCriterial());
            }
        }
        ResultSet result = preparedStatement.executeQuery();
        while (result.next()) {
            Category category = new Category(result.getInt(1), result.getString(2));
            queryResult.add(category);
        }
        return queryResult;
    }

    @Override
    public int create(Category category) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        String sqlCreateInstance = "INSERT INTO category (title) VALUES(?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlCreateInstance, PreparedStatement.RETURN_GENERATED_KEYS);
        Object[] instance = category.getObjects();
        preparedStatement.setString(1, (String) instance[1]);
        preparedStatement.executeUpdate();
        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        generatedKeys.next();
        return generatedKeys.getInt(1);
    }

    @Override
    public int delete(int id) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        String sqlDeleteInstance = "DELETE FROM category WHERE id=?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteInstance);
        preparedStatement.setInt(1, id);
        return preparedStatement.executeUpdate();
    }

    @Override
    public int update(Category category) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        String sqlUpdateInstance = "UPDATE category SET title=? WHERE id=?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdateInstance);
        Object[] instance = category.getObjects();
        preparedStatement.setString(1, (String) instance[1]);
        preparedStatement.setInt(2, (int) instance[0]);
        return preparedStatement.executeUpdate();
    }
}
