package news.web.controllers;

import news.dto.CategorySerializer;
import news.service.CategoryService;
import news.web.http.HttpRequest;
import news.web.http.HttpResponse;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CategoryController implements Controller {
    HttpRequest request;
    HttpResponse response = new HttpResponse();
    CategoryService categoryService;
    CategorySerializer categorySerializer;

    public CategoryController(CategoryService categoryService, HttpRequest request) {
        this.categoryService = categoryService;
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
                System.out.println("Получение по id, редактирование или удаление в зависимости от типп зпроса");
            // поиск по title
            } else {
                System.out.println("Поиск по title");
            }
        } else {
            System.out.println("Добавляем новую запись или получаем список записей");
            switch (request.getMethod()) {
                case ("GET"):
                    System.out.println("Получаем список записей (GET)");
                    break;
                case ("POST"):
                    try {
                        System.out.println("Добавляем запись (POST)");
                        categorySerializer = new CategorySerializer(request.getBody());
                        int id = categoryService.create(categorySerializer.toObject());
                        response.setStatusCode(201);
                        response.setStatusText("Категория создана");
                        response.setVersion("HTTP/1.1");
                        response.setHeader("Location", String.format("/category/%s", id));
                        /*response.setBody("<!DOCTYPE>\n" +
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
                                "</html>");*/
                    } catch (Exception e) {
                        response.setStatusCode(405);
                        response.setStatusText("Ошибка добавления");
                        response.setVersion("HTTP/1.1");
                        System.out.println("Ошибка добавления!");
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
