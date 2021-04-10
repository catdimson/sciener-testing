package news.dao.repositories;

import news.dao.specifications.SqlSpecification;

import java.sql.SQLException;
import java.util.List;

public interface Repository<T> {

    List<T> query(SqlSpecification<T> specification) throws SQLException;

    void create(T instance) throws SQLException;
    void delete(int id) throws SQLException;
    void update(T instance) throws SQLException;

}
