package news;

import news.dao.connection.DBPool;
import news.web.controllers.RootController;
import news.web.http.HttpRequest;
import news.web.http.HttpResponse;

import java.io.IOException;
import java.sql.SQLException;

public class NewsApp extends Thread {
    HttpRequest request;
    DBPool dbPool;
    HttpResponse response;

    public NewsApp(HttpRequest request) throws IOException, SQLException {
        this.request = request;
        dbPool = new DBPool();
        RootController rootController = new RootController(request, dbPool);
        response = rootController.getResponse();
    }

    public NewsApp(HttpRequest request, DBPool dbPool) throws IOException, SQLException {
        this.request = request;
        this.dbPool = dbPool;
        RootController rootController = new RootController(request, dbPool);
        response = rootController.getResponse();
    }

    public DBPool getDBPool() {
        return dbPool;
    }

    public HttpResponse getResponse() {
        return response;
    }
}
