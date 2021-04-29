package news.web.controllers;

import news.web.http.HttpResponse;

import java.sql.SQLException;

public interface Controller {

    void buildResponse() throws SQLException;

    HttpResponse getResponse();

}
