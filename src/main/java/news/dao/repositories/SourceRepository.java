package news.dao.repositories;

import news.dao.connection.ConnectionPool;
import news.dao.connection.DBPool;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Source;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SourceRepository implements ExtendRepository<Source> {
    final private ConnectionPool connectionPool;

    public SourceRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Source> query(ExtendSqlSpecification<Source> sourceSpecification) throws SQLException {
        List<Source> queryResult = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        String sqlQuery = sourceSpecification.toSqlClauses();
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        if (sourceSpecification.isById()) {
            preparedStatement.setInt(1, (int) sourceSpecification.getCriterial());
        } else {
            if (sourceSpecification.getCriterial() != null) {
                preparedStatement.setString(1, (String) sourceSpecification.getCriterial());
            }
        }
        ResultSet result = preparedStatement.executeQuery();
        while (result.next()) {
            Source source = new Source(result.getInt(1), result.getString(2), result.getString(3));
            queryResult.add(source);
        }
        return queryResult;
    }

    @Override
    public int create(Source source) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        String sqlCreateInstance = "INSERT INTO source(title, url) VALUES(?, ?);";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlCreateInstance, Statement.RETURN_GENERATED_KEYS);
        Object[] instance = source.getObjects();
        preparedStatement.setString(1, (String) instance[1]);
        preparedStatement.setString(2, (String) instance[2]);
        preparedStatement.executeUpdate();
        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        generatedKeys.next();
        return generatedKeys.getInt(1);
    }

    @Override
    public int delete(int id) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        String sqlDeleteInstance = "DELETE FROM source WHERE id=?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteInstance);
        preparedStatement.setInt(1, id);
        return preparedStatement.executeUpdate();
    }

    @Override
    public int update(Source source) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        String sqlUpdateInstance = "UPDATE source SET title=?, url=? WHERE id=?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdateInstance);
        Object[] instance = source.getObjects();
        preparedStatement.setString(1, (String) instance[1]);
        preparedStatement.setString(2, (String) instance[2]);
        preparedStatement.setInt(3, (int) instance[0]);
        return preparedStatement.executeUpdate();
    }
}
