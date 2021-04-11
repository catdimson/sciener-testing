package news.dao.repositories;

import news.dao.connection.DBPool;
import news.dao.specifications.SqlSpecification;
import news.model.Source;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SourceRepository implements Repository<Source> {
    final private DBPool connectionPool;

    public SourceRepository(DBPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Source> query(SqlSpecification<Source> sourceSpecification) throws SQLException {
        List<Source> queryResult = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        Statement statement = connection.createStatement();
        String sqlQuery = sourceSpecification.toSqlClauses();
        ResultSet result = statement.executeQuery(sqlQuery);
        while (result.next()) {
            Source source = new Source(result.getInt(1), result.getString(2), result.getString(3));
            queryResult.add(source);
        }
        return queryResult;
    }

    @Override
    public void create(Source source) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Statement statement = connection.createStatement();
        Object[] instance = source.getObjects();
        String sqlCreateInstance = String.format("INSERT INTO source(title, url) VALUES('%s', '%s')", instance[1], instance[2]);
        statement.executeUpdate(sqlCreateInstance);
    }

    @Override
    public void delete(int id) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Statement statement = connection.createStatement();
        String sqlDeleteInstance = String.format("DELETE FROM source WHERE id=%d;", id);
        statement.executeUpdate(sqlDeleteInstance);
    }

    @Override
    public void update(Source source) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Statement statement = connection.createStatement();
        Object[] instance = source.getObjects();
        String sqlUpdateInstance = String.format("UPDATE source SET title='%s', url='%s' WHERE id=%s;", instance[1], instance[2], instance[0]);
        statement.executeUpdate(sqlUpdateInstance);
    }
}
