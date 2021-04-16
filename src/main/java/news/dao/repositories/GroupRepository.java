package news.dao.repositories;

import news.dao.connection.DBPool;
import news.dao.specifications.SqlSpecification;
import news.model.Group;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GroupRepository implements Repository<Group> {
    final private DBPool connectionPool;

    public GroupRepository(DBPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Group> query(SqlSpecification<Group> groupSpecification) throws SQLException {
        List<Group> queryResult = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        Statement statement = connection.createStatement();
        String sqlQuery = groupSpecification.toSqlClauses();
        ResultSet result = statement.executeQuery(sqlQuery);
        while (result.next()) {
            Group group = new Group(result.getInt(1), result.getString(2));
            queryResult.add(group);
        }
        return queryResult;
    }

    @Override
    public void create(Group group) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Statement statement = connection.createStatement();
        Object[] instance = group.getObjects();
        String sqlCreateInstance = String.format("INSERT INTO \"group\"(title) VALUES('%s')", instance[1]);
        statement.executeUpdate(sqlCreateInstance);
    }

    @Override
    public void delete(int id) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Statement statement = connection.createStatement();
        String sqlDeleteInstance = String.format("DELETE FROM \"group\" WHERE id=%d;", id);
        statement.executeUpdate(sqlDeleteInstance);
    }

    @Override
    public void update(Group group) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Statement statement = connection.createStatement();
        Object[] instance = group.getObjects();
        String sqlUpdateInstance = String.format("UPDATE \"group\" SET title='%s' WHERE id=%s;", instance[1], instance[0]);
        statement.executeUpdate(sqlUpdateInstance);
    }
}
