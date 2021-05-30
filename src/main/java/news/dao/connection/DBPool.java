package news.dao.connection;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Properties;

public class DBPool implements ConnectionPool {
    private String dbUser;
    private String dbPassword;
    private String dbConnectionUrl;
    final private Deque<Connection> connectionPool = new ArrayDeque<>();

    public DBPool() throws IOException {
        // вариант с загрузкой параметров подключения к БД из файла .properties
        URL path = getClass().getClassLoader().getResource("dbconnection.properties");
        FileInputStream fileProperties = new FileInputStream(path.getPath());
        Properties prop = new Properties();
        prop.load(fileProperties);
        this.dbUser = new String (prop.getProperty("DB_USER").getBytes("ISO8859-1"));
        this.dbPassword = new String(prop.getProperty("DB_PASSWORD").getBytes("ISO8859-1"));
        this.dbConnectionUrl = new String(prop.getProperty("DB_CONNECTION_URL").getBytes("ISO8859-1"));

        // вариант с получением параметров подключения к БД из командной строки
        /*String commandLine = System.getProperty("sun.java.command");
        String[] commandArgs = commandLine.split(" ");
        this.dbUser = commandArgs[1];
        this.dbPassword = commandArgs[2];
        this.dbConnectionUrl = commandArgs[3];*/
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
            try {
                Class.forName("org.postgresql.Driver");
                return DriverManager.getConnection(this.dbConnectionUrl, this.dbUser, this.dbPassword);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void pullConnection(Connection connection) {
        this.connectionPool.push(connection);
    }
}
