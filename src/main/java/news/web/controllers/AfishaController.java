package news.web.controllers;

import news.dto.AfishaSerializer;
import news.service.AfishaService;
import news.web.http.HttpRequest;
import news.web.http.HttpResponse;

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
    public void buildResponse() {
        String urlUnit = request.getPath(true);
        Pattern p = Pattern.compile("^/.+/(.+)/");
        Matcher m = p.matcher(urlUnit);

        // работаем с конкретной записью
        if (m.find()) {
            p = Pattern.compile("^/.+/(\\d+)/");
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
                        int id = afishaService.create(afishaSerializer.toObject());
                        response.setStatusCode(201);
                        response.setStatusText("Афиша создана");
                        response.setVersion("HTTP/1.1");
                        response.setHeader("Location", String.format("/afisha/%s/", id));
                        response.setBody("<!DOCTYPE>\n" +
                                "<html>\n" +
                                "<head>\n" +
                                "<title>Работает!</title>\n" +
                                "<meta charset=\"utf-8\">\n" +
                                "</head>\n" +
                                "<body>\n" +
                                "<h1>Ответ пришел</h1>\n" +
                                "<p>Your browser sent a request that this server could not understand.</p>\n" +
                                "<p>The request line contained invalid characters following the protocol string.</p>\n" +
                                "</body>\n" +
                                "</html>");
                    } catch (Exception e) {
                        response.setStatusCode(405);
                        response.setStatusText("Ошибка добавления");
                        response.setVersion("HTTP/1.1");
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
