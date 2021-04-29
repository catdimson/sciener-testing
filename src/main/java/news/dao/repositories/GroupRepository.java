package news.dao.repositories;

import news.dao.connection.DBPool;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Group;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupRepository implements ExtendRepository<Group> {
    final private DBPool connectionPool;

    public GroupRepository(DBPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Group> query(ExtendSqlSpecification<Group> groupSpecification) throws SQLException {
        List<Group> queryResult = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        String sqlQuery = groupSpecification.toSqlClauses();
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        if (groupSpecification.isById()) {
            preparedStatement.setInt(1, (int) groupSpecification.getCriterial());
        } else {
            preparedStatement.setString(1, (String) groupSpecification.getCriterial());
        }
        ResultSet result = preparedStatement.executeQuery();
        while (result.next()) {
            Group group = new Group(result.getInt(1), result.getString(2));
            queryResult.add(group);
        }
        return queryResult;
    }

    @Override
    public int create(Group group) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        String sqlCreateInstance = "INSERT INTO \"group\" (title) VALUES(?);";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlCreateInstance, Statement.RETURN_GENERATED_KEYS);
        Object[] instance = group.getObjects();
        preparedStatement.setString(1, (String) instance[1]);
        preparedStatement.executeUpdate();
        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        generatedKeys.next();
        return generatedKeys.getInt(1);
    }

    @Override
    public void delete(int id) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        String sqlDeleteInstance = "DELETE FROM \"group\" WHERE id=?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteInstance);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    @Override
    public void update(Group group) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        String sqlUpdateInstance = "UPDATE \"group\" SET title=? WHERE id=?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdateInstance);
        Object[] instance = group.getObjects();
        preparedStatement.setString(1, (String) instance[1]);
        preparedStatement.setInt(2, (int) instance[0]);
        preparedStatement.executeUpdate();
    }
}
