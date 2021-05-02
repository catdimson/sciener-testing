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
                } else {
                    response.setStatusCode(400);
                    response.setVersion("HTTP/1.1");
                    response.setStatusText("Некорректный запрос");
                    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                    response.setHeader("Pragma", "no-cache");
                }
                break;
            case "POST":
                // создание записи
                p = Pattern.compile("^/category/$");
                m = p.matcher(url);
                if (m.find()) {
                    categorySerializer = new CategorySerializer(request.getBody());
                    int id = categoryService.create(categorySerializer.toObject());
                    response.setStatusCode(201);
                    response.setStatusText("Категория создана");
                    response.setVersion("HTTP/1.1");
                    response.setHeader("Location", String.format("/category/%s/", id));
                } else {
                    response.setStatusCode(400);
                    response.setVersion("HTTP/1.1");
                    response.setStatusText("Некорректный запрос");
                }
                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                break;
            case "PUT":
                System.out.println("PUT");
                p = Pattern.compile("^/category/(?<id>(\\d+))/$");
                m = p.matcher(url);
                // если status=0 - не было выполнено обновление, если не 0 - выполнено
                int statusUpdate;
                if (m.find()) {
                    categorySerializer = new CategorySerializer(request.getBody());
                    // нужно сравнить id из url и id из body. Если не совпадают то вернуть ответ с "Некорректный запрос"
                    Category category = categorySerializer.toObject();
                    int idCategoryFromBody = (int) category.getObjects()[0];
                    if (Integer.parseInt(m.group("id")) != idCategoryFromBody) {
                        /*System.out.println("--------------------------------");
                        System.out.println("400 Некорректный запрос");
                        System.out.println("--------------------------------");*/
                        response.setStatusCode(400);
                        response.setVersion("HTTP/1.1");
                        response.setStatusText("Некорректный запрос");
                    } else {
                        statusUpdate = categoryService.update(categorySerializer.toObject());
                        if (statusUpdate != 0) {
                            response.setStatusCode(204);
                            response.setStatusText("Нет данных");
                        } else {
                            response.setStatusCode(404);
                            response.setStatusText("Категория для обновления не найдена");
                        }
                    }
                    response.setVersion("HTTP/1.1");
                } else {
                    response.setStatusCode(400);
                    response.setVersion("HTTP/1.1");
                    response.setStatusText("Некорректный запрос");
                }
                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                break;
            case "DELETE":
                System.out.println("DELETE!");
                p = Pattern.compile("^/category/(?<id>(\\d+))/$");
                m = p.matcher(url);
                if (m.find()) {
                    int id = Integer.parseInt(m.group("id"));
                    int statusDelete;
                    statusDelete = categoryService.delete(id);
                    if (statusDelete != 0) {
                        response.setStatusCode(204);
                        response.setStatusText("Нет данных");
                    } else {
                        response.setStatusCode(404);
                        response.setStatusText("Категория для удаления не найдена");
                    }
                } else {
                    response.setStatusCode(400);
                    response.setStatusText("Некорректный запрос");
                }
                response.setVersion("HTTP/1.1");
                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                break;
            default:
                response.setStatusCode(400);
                response.setVersion("HTTP/1.1");
                response.setStatusText("Некорректный запрос");
                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                response.setHeader("Pragma", "no-cache");
        }
    }

    @Override
    public HttpResponse getResponse() {
        return response;
    }
}
