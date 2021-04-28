package news.web.controllers;

import news.dto.AfishaSerializer;
import news.service.AfishaService;
import news.web.http.HttpRequest;
import news.web.http.HttpResponse;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AfishaController implements Controller {
    HttpRequest request;
    HttpResponse response;
    AfishaService afishaService;
    AfishaSerializer afishaSerializer;

    public AfishaController(AfishaService afishaService, HttpRequest request) {
        this.afishaService = afishaService;
        this.request = request;
    }

    @Override
    public void buildResponse() throws SQLException {
        String urlUnit = request.getPath();
        Pattern p = Pattern.compile("/.+/(.+)/");
        Matcher m = p.matcher(urlUnit);

        // работаем с конкретной записью
        if (m.find()) {
            p = Pattern.compile("/.+/(\\d+)/");
            m = p.matcher(urlUnit);
            // получение по id, редактирование, удаление записи
            if (m.find()) {
                System.out.println("Получение по id, редактирование или удаление");
            // поиск по title
            } else {
                /* КОД ПОИСКА ПО TITLE */
            }
        } else {
            System.out.println("Добавляем новую запись или получаем список");
            switch (request.getMethod()) {
                case ("GET"):
                    System.out.println("Получаем список записей (GET)");
                    break;
                case ("POST"):
                    try {
                        afishaSerializer = new AfishaSerializer(request.getBody());
                        afishaService.create(afishaSerializer.toObject());
                    } catch (Exception e) {
                        response.setStatusCode(405);
                        response.setStatusText("Ошибка добавления");
                        response.setVersion("HTTP/1.1");
                        //response.setHeader();
                    }


                    break;
                default:
                    // ошибка
                    break;
            }
        }
    }

    @Override
    public HttpResponse getResponse() {
        return response;
    }
}
