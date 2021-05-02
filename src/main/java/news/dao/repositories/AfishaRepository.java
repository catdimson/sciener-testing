package news.dao.repositories;

import news.dao.connection.DBPool;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Afisha;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AfishaRepository implements ExtendRepository<Afisha> {
    final private DBPool connectionPool;

    public AfishaRepository(DBPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Afisha> query(ExtendSqlSpecification<Afisha> afishaSpecification) throws SQLException {
        List<Afisha> queryResult = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        String sqlQuery = afishaSpecification.toSqlClauses();
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        if (afishaSpecification.isById()) {
            preparedStatement.setInt(1, (int) afishaSpecification.getCriterial());
        } else {
            if (afishaSpecification.getCriterial() != null) {
                preparedStatement.setString(1, (String) afishaSpecification.getCriterial());
            }
        }
        ResultSet result = preparedStatement.executeQuery();
        while (result.next()) {
            Afisha afisha = new Afisha(
                    result.getInt(1),
                    result.getString(2),
                    result.getString(3),
                    result.getString(4),
                    result.getString(5),
                    result.getString(6),
                    result.getString(7),
                    result.getString(8),
                    result.getString(9),
                    result.getTimestamp(10).toLocalDateTime().toLocalDate(),
                    result.getBoolean(11),
                    result.getInt(12),
                    result.getInt(13));
            queryResult.add(afisha);
        }
        return queryResult;
    }

    @Override
    public int create(Afisha afisha) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        String sqlCreateInstance = "INSERT INTO afisha" +
                "(title, image_url, lead, description, age_limit, timing, place, phone, date, is_commercial, user_id, source_id) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement statement = connection.prepareStatement(sqlCreateInstance, PreparedStatement.RETURN_GENERATED_KEYS);
        Object[] instance = afisha.getObjects();
        statement.setString(1, (String) instance[1]);
        statement.setString(2, (String) instance[2]);
        statement.setString(3, (String) instance[3]);
        statement.setString(4, (String) instance[4]);
        statement.setString(5, (String) instance[5]);
        statement.setString(6, (String) instance[6]);
        statement.setString(7, (String) instance[7]);
        statement.setString(8, (String) instance[8]);
        LocalDate date = (LocalDate) instance[9];
        statement.setTimestamp(9, Timestamp.valueOf(date.atStartOfDay()));
        statement.setBoolean(10, (Boolean) instance[10]);
        statement.setInt(11, (int) instance[11]);
        statement.setInt(12, (int) instance[12]);
        statement.executeUpdate();
        ResultSet generatedKeys = statement.getGeneratedKeys();
        generatedKeys.next();
        return generatedKeys.getInt(1);
    }

    @Override
    public int delete(int id) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        String sqlDeleteInstance = "DELETE FROM afisha WHERE id=?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteInstance);
        preparedStatement.setInt(1, id);
        return preparedStatement.executeUpdate();
    }

    @Override
    public int update(Afisha afisha) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        String sqlUpdateInstance = "UPDATE afisha SET " +
                "title=?, image_url=?, lead=?, description=?, age_limit=?, timing=?, place=?, phone=?, " +
                "date=?, is_commercial=?, user_id=?, source_id=? WHERE id=?;";
        PreparedStatement statement = connection.prepareStatement(sqlUpdateInstance);
        Object[] instance = afisha.getObjects();
        statement.setString(1, (String) instance[1]);
        statement.setString(2, (String) instance[2]);
        statement.setString(3, (String) instance[3]);
        statement.setString(4, (String) instance[4]);
        statement.setString(5, (String) instance[5]);
        statement.setString(6, (String) instance[6]);
        statement.setString(7, (String) instance[7]);
        statement.setString(8, (String) instance[8]);
        LocalDate date = (LocalDate) instance[9];
        statement.setTimestamp(9, Timestamp.valueOf(date.atStartOfDay()));
        statement.setBoolean(10, (Boolean) instance[10]);
        statement.setInt(11, (int) instance[11]);
        statement.setInt(12, (int) instance[12]);
        statement.setInt(13, (int) instance[0]);
        return statement.executeUpdate();
    }
}
