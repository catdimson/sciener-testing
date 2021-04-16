package news.dao.repositories;

import news.dao.connection.DBPool;
import news.dao.specifications.SqlSpecification;
import news.model.Tag;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TagRepository implements Repository<Tag> {
    final private DBPool connectionPool;

    public TagRepository(DBPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Tag> query(SqlSpecification<Tag> tagSpecification) throws SQLException {
        List<Tag> queryResult = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        Statement statement = connection.createStatement();
        String sqlQuery = tagSpecification.toSqlClauses();
        ResultSet result = statement.executeQuery(sqlQuery);
        while (result.next()) {
            Tag tag = new Tag(result.getInt(1), result.getString(2));
            queryResult.add(tag);
        }
        return queryResult;
    }

    @Override
    public void create(Tag tag) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Statement statement = connection.createStatement();
        Object[] instance = tag.getObjects();
        String sqlCreateInstance = String.format("INSERT INTO tag(title) VALUES('%s')", instance[1]);
        statement.executeUpdate(sqlCreateInstance);
    }

    @Override
    public void delete(int id) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Statement statement = connection.createStatement();
        String sqlDeleteInstance = String.format("DELETE FROM tag WHERE id=%d;", id);
        statement.executeUpdate(sqlDeleteInstance);
    }

    @Override
    public void update(Tag tag) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Statement statement = connection.createStatement();
        Object[] instance = tag.getObjects();
        String sqlUpdateInstance = String.format("UPDATE tag SET title='%s' WHERE id=%s;", instance[1], instance[0]);
        statement.executeUpdate(sqlUpdateInstance);
    }
}
