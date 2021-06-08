package news.web.controllers;

import news.web.http.HttpRequest;
import news.web.http.HttpResponse;

import java.sql.SQLException;

@Deprecated
public interface Controller {

    void buildResponse(HttpRequest request) throws SQLException;

    HttpResponse getResponse();

}
