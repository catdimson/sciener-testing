package news.dao.repositories;

import news.dao.connection.DBPool;
import news.dao.specifications.SqlSpecification;
import news.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements Repository<User> {
    final private DBPool connectionPool;

    public UserRepository(DBPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<User> query(SqlSpecification<User> userSpecification) throws SQLException {
        List<User> queryResult = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        Statement statement = connection.createStatement();
        String sqlQuery = userSpecification.toSqlClauses();
        ResultSet result = statement.executeQuery(sqlQuery);
        while (result.next()) {
            User user = new User(
                    result.getInt(1),
                    result.getString(2),
                    result.getString(3),
                    result.getString(4),
                    result.getString(5),
                    result.getString(6),
                    result.getTimestamp(7).toLocalDateTime().toLocalDate(),
                    result.getTimestamp(8).toLocalDateTime().toLocalDate(),
                    result.getBoolean(9),
                    result.getBoolean(10),
                    result.getBoolean(11),
                    result.getInt(12));
            queryResult.add(user);
        }
        return queryResult;
    }

    @Override
    public void create(User user) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Object[] instance = user.getObjects();
        String sqlCreateInstance = "INSERT INTO \"user\"" +
                "(password, username, first_name, last_name, email, last_login, date_joined, is_superuser, is_staff, is_active, group_id) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        PreparedStatement statement = connection.prepareStatement(sqlCreateInstance);
        statement.setString(1, (String) instance[1]);
        statement.setString(2, (String) instance[2]);
        statement.setString(3, (String) instance[3]);
        statement.setString(4, (String) instance[4]);
        statement.setString(5, (String) instance[5]);
        LocalDate dateLogin = (LocalDate) instance[6];
        statement.setTimestamp(6, Timestamp.valueOf(dateLogin.atStartOfDay()));
        LocalDate dateJoined = (LocalDate) instance[7];
        statement.setTimestamp(7, Timestamp.valueOf(dateJoined.atStartOfDay()));
        statement.setBoolean(8, (Boolean) instance[8]);
        statement.setBoolean(9, (Boolean) instance[9]);
        statement.setBoolean(10, (Boolean) instance[10]);
        statement.setInt(11, (int) instance[11]);
        statement.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Statement statement = connection.createStatement();
        String sqlDeleteInstance = String.format("DELETE FROM \"user\" WHERE id=%d;", id);
        statement.executeUpdate(sqlDeleteInstance);
    }

    @Override
    public void update(User user) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Object[] instance = user.getObjects();
        String sqlUpdateInstance = "UPDATE \"user\" SET " +
                "password=?, username=?, first_name=?, last_name=?, email=?, last_login=?, date_joined=?, is_superuser=?, " +
                "is_staff=?, is_active=?, group_id=? WHERE id=?;";

        PreparedStatement statement = connection.prepareStatement(sqlUpdateInstance);
        statement.setString(1, (String) instance[1]);
        statement.setString(2, (String) instance[2]);
        statement.setString(3, (String) instance[3]);
        statement.setString(4, (String) instance[4]);
        statement.setString(5, (String) instance[5]);
        LocalDate dateLogin = (LocalDate) instance[6];
        statement.setTimestamp(6, Timestamp.valueOf(dateLogin.atStartOfDay()));
        LocalDate dateJoined = (LocalDate) instance[7];
        statement.setTimestamp(7, Timestamp.valueOf(dateJoined.atStartOfDay()));
        statement.setBoolean(8, (Boolean) instance[8]);
        statement.setBoolean(9, (Boolean) instance[9]);
        statement.setBoolean(10, (Boolean) instance[10]);
        statement.setInt(11, (int) instance[11]);
        statement.setInt(12, (int) instance[0]);
        statement.executeUpdate();
    }
}
