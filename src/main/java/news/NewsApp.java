package news;

import news.dao.connection.ConnectionPool;
import news.dao.connection.DBPool;
import news.web.controllers.RootController;
import news.web.http.HttpRequest;
import news.web.http.HttpResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class NewsApp extends Thread {
    HttpRequest request;
    ConnectionPool dbPool;
    HttpResponse response;

    public NewsApp(HttpRequest request) throws IOException, SQLException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.request = request;
        dbPool = new DBPool();
        RootController rootController = new RootController(request, dbPool);
        response = rootController.getResponse();
    }

    public NewsApp(HttpRequest request, ConnectionPool dbPool) throws IOException, SQLException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.request = request;
        this.dbPool = dbPool;
        RootController rootController = new RootController(request, dbPool);
        response = rootController.getResponse();
    }

    public ConnectionPool getDBPool() {
        return dbPool;
    }

    public HttpResponse getResponse() {
        return response;
    }
}
