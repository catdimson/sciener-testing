package news.service;

import news.dao.specifications.ExtendSqlSpecification;

import java.sql.SQLException;
import java.util.List;

public interface Service<T> {

    List<T> query(ExtendSqlSpecification<T> specification) throws SQLException;

    int create(T instance) throws SQLException;
    int delete(int id) throws SQLException;
    int update(T instance) throws SQLException;
}
