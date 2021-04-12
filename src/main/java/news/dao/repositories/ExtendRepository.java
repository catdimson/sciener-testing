package news.dao.repositories;

import news.dao.specifications.ExtendSqlSpecification;

import java.sql.SQLException;
import java.util.List;

public interface ExtendRepository<T> {

    List<T> query(ExtendSqlSpecification<T> specification) throws SQLException;

    void create(T instance) throws SQLException;
    void delete(int id) throws SQLException;
    void update(T instance) throws SQLException;

}
