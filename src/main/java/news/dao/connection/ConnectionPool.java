package news.dao.connection;

import java.sql.Connection;
import java.sql.SQLException;

@Deprecated
public interface ConnectionPool {
    Connection getConnection() throws SQLException;
}
