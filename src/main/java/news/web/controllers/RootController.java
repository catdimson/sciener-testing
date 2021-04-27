package news.web.controllers;

import news.web.http.HttpRequest;
import news.web.http.HttpResponse;

public class RootController {
    HttpRequest request;
    HttpResponse response;

    public RootController(HttpRequest request) {
        this.request = request;
    }

    public void autodetect() {

    }
}
