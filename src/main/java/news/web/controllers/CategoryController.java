package news.web.controllers;

import news.dao.specifications.FindByIdCategorySpecification;
import news.dto.CategorySerializer;
import news.model.Category;
import news.service.CategoryService;
import news.web.http.HttpRequest;
import news.web.http.HttpResponse;

import java.sql.SQLException;
import java.util.List;
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
        String url = request.getPath();
        Pattern p;
        Matcher m;

        // работаем с конкретной записью
        switch(request.getMethod()) {
            case "GET":
                p = Pattern.compile("^/category/(?<id>(\\d+))/$");
                m = p.matcher(url);
                if (m.find()) {
                    FindByIdCategorySpecification findById = new FindByIdCategorySpecification(Integer.parseInt(m.group("id")));
                    List<Category> findByIdCategoryList = categoryService.query(findById);
                    if (findByIdCategoryList.isEmpty()) {
                        response.setStatusCode(404);
                        response.setStatusText("Категория не найдена");
                        response.setVersion("HTTP/1.1");
                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                        response.setHeader("Pragma", "no-cache");
                        response.setBody("[]");
                    } else {
                        Category category = findByIdCategoryList.get(0);
                        categorySerializer = new CategorySerializer(category);
                        response.setStatusCode(200);
                        response.setStatusText("OK");
                        response.setVersion("HTTP/1.1");
                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                        response.setHeader("Pragma", "no-cache");
                        response.setBody(categorySerializer.toJSON());
                    }
                }
                break;
            case "POST":
                System.out.println(url);
                p = Pattern.compile("^/category/$");
                m = p.matcher(url);
                if (m.find()) {
                    categorySerializer = new CategorySerializer(request.getBody());
                    int id = categoryService.create(categorySerializer.toObject());
                    response.setStatusCode(201);
                    response.setStatusText("Категория создана");
                    response.setVersion("HTTP/1.1");
                    response.setHeader("Location", String.format("/category/%s/", id));
                }
                break;
            case "PUT":
                // код
                break;
            case "DELETE":
                // код
                break;
            default:
                // данный метод не поддерживается
        }


        /*if (m.find()) {
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
                        response.setHeader("Location", String.format("/category/%s/", id));
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
        }*/
    }

    @Override
    public HttpResponse getResponse() {
        return response;
    }
}
