package news;

import news.dao.connection.DBPool;
import news.web.controllers.RootController;
import news.web.http.HttpRequest;

import java.io.IOException;
import java.sql.SQLException;

public class NewsApp extends Thread {
    HttpRequest request;
    DBPool dbPool;

    public NewsApp(HttpRequest request) throws IOException, SQLException {
        this.request = request;
        dbPool = new DBPool();
        RootController rootController = new RootController(request, dbPool);
        rootController.getResponse();
    }
}
