package news.dao.repositories;

import news.dao.connection.DBPool;
import news.dao.specifications.SqlSpecification;
import news.model.Mailing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MailingRepository implements Repository<Mailing> {
    final private DBPool connectionPool;

    public MailingRepository(DBPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Mailing> query(SqlSpecification<Mailing> mailingSpecification) throws SQLException {
        List<Mailing> queryResult = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        Statement statement = connection.createStatement();
        String sqlQuery = mailingSpecification.toSqlClauses();
        ResultSet result = statement.executeQuery(sqlQuery);
        while (result.next()) {
            Mailing mailing = new Mailing(result.getInt(1), result.getString(2));
            queryResult.add(mailing);
        }
        return queryResult;
    }

    @Override
    public void create(Mailing mailing) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Statement statement = connection.createStatement();
        Object[] instance = mailing.getObjects();
        String sqlCreateInstance = String.format("INSERT INTO mailing(email) VALUES('%s')", instance[1]);
        statement.executeUpdate(sqlCreateInstance);
    }

    @Override
    public void delete(int id) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Statement statement = connection.createStatement();
        String sqlDeleteInstance = String.format("DELETE FROM mailing WHERE id=%d;", id);
        statement.executeUpdate(sqlDeleteInstance);
    }

    @Override
    public void update(Mailing mailing) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Statement statement = connection.createStatement();
        Object[] instance = mailing.getObjects();
        String sqlUpdateInstance = String.format("UPDATE mailing SET email='%s' WHERE id=%s;", instance[1], instance[0]);
        statement.executeUpdate(sqlUpdateInstance);
    }
}
