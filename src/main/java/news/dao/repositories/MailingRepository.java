package news.dao.repositories;

import news.dao.connection.ConnectionPool;
import news.dao.connection.DBPool;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Mailing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MailingRepository implements ExtendRepository<Mailing> {
    final private ConnectionPool connectionPool;

    public MailingRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Mailing> query(ExtendSqlSpecification<Mailing> mailingSpecification) throws SQLException {
        List<Mailing> queryResult = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        String sqlQuery = mailingSpecification.toSqlClauses();
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        if (mailingSpecification.isById()) {
            preparedStatement.setInt(1, (int) mailingSpecification.getCriterial());
        } else {
            if (mailingSpecification.getCriterial() != null) {
                preparedStatement.setString(1, (String) mailingSpecification.getCriterial());
            }
        }
        ResultSet result = preparedStatement.executeQuery();
        while (result.next()) {
            Mailing mailing = new Mailing(result.getInt(1), result.getString(2));
            queryResult.add(mailing);
        }
        return queryResult;
    }

    @Override
    public int create(Mailing mailing) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        String sqlCreateInstance = "INSERT INTO mailing(email) VALUES(?);";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlCreateInstance, Statement.RETURN_GENERATED_KEYS);
        Object[] instance = mailing.getObjects();
        preparedStatement.setString(1, (String) instance[1]);
        preparedStatement.executeUpdate();
        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        generatedKeys.next();
        return generatedKeys.getInt(1);
    }

    @Override
    public int delete(int id) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        String sqlDeleteInstance = "DELETE FROM mailing WHERE id=?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteInstance);
        preparedStatement.setInt(1, id);
        return preparedStatement.executeUpdate();
    }

    @Override
    public int update(Mailing mailing) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        String sqlUpdateInstance = "UPDATE mailing SET email=? WHERE id=?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdateInstance);
        Object[] instance = mailing.getObjects();
        preparedStatement.setString(1, (String) instance[1]);
        preparedStatement.setInt(2, (int) instance[0]);
        return preparedStatement.executeUpdate();
    }
}
