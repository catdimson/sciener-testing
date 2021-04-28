package news.web.controllers;

import news.web.http.HttpResponse;

import java.sql.SQLException;

public interface Controller {

    public void buildResponse() throws SQLException;

    public HttpResponse getResponse();

}
