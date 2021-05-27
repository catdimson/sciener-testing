package news.dao.repositories;

import news.dao.connection.ConnectionPool;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Tag;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagRepository implements ExtendRepository<Tag> {
    final private ConnectionPool connectionPool;

    public TagRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Tag> query(ExtendSqlSpecification<Tag> tagSpecification) throws SQLException {
        List<Tag> queryResult = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        String sqlQuery = tagSpecification.toSqlClauses();
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        if (tagSpecification.isById()) {
            preparedStatement.setInt(1, (int) tagSpecification.getCriterial());
        } else {
            if (tagSpecification.getCriterial() != null) {
                preparedStatement.setString(1, (String) tagSpecification.getCriterial());
            }
        }
        ResultSet result = preparedStatement.executeQuery();
        while (result.next()) {
            Tag tag = new Tag(result.getInt(1), result.getString(2));
            queryResult.add(tag);
        }
        return queryResult;
    }

    @Override
    public int create(Tag tag) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        String sqlCreateInstance = "INSERT INTO tag (title) VALUES(?);";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlCreateInstance, Statement.RETURN_GENERATED_KEYS);
        Object[] instance = tag.getObjects();
        preparedStatement.setString(1, (String) instance[1]);
        preparedStatement.executeUpdate();
        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        generatedKeys.next();
        return generatedKeys.getInt(1);
    }

    @Override
    public int delete(int id) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        String sqlDeleteInstance = "DELETE FROM tag WHERE id=?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteInstance);
        preparedStatement.setInt(1, id);
        return preparedStatement.executeUpdate();
    }

    @Override
    public int update(Tag tag) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        String sqlUpdateInstance = "UPDATE tag SET title=? WHERE id=?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdateInstance);
        Object[] instance = tag.getObjects();
        preparedStatement.setString(1, (String) instance[1]);
        preparedStatement.setInt(2, (int) instance[0]);
        return preparedStatement.executeUpdate();
    }
}
