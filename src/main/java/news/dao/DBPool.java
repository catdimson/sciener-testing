package news.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Properties;

public class DBPool implements ConnectionPool {
    private String dbUser;
    private String dbPassword;
    private String dbConnectionUrl;
    final private ArrayDeque<Connection> connectionPool = new ArrayDeque<>();

    public DBPool() {
        try {
            FileInputStream fileProperties = new FileInputStream("src/main/resources/dbconnection.properties");
            Properties prop = new Properties();
            prop.load(fileProperties);

            this.dbUser = new String (prop.getProperty("DB_USER").getBytes("ISO8859-1"));
            this.dbPassword = new String(prop.getProperty("DB_PASSWORD").getBytes("ISO8859-1"));
            this.dbConnectionUrl = new String(prop.getProperty("DB_CONNECTION_URL").getBytes("ISO8859-1"));
        } catch (IOException throwable) {
            throwable.printStackTrace();
        }
    }

    public DBPool(String connectionUrl, String username, String password) {
        this.dbConnectionUrl = connectionUrl;
        this.dbUser = username;
        this.dbPassword = password;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = this.connectionPool.pollLast();
        if (connection != null) {
            return connection;
        } else {
            return DriverManager.getConnection(this.dbConnectionUrl, this.dbUser, this.dbPassword);
        }
    }

    public void pullConnection(Connection connection) {
        this.connectionPool.push(connection);
    }
}
