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
                p = Pattern.compile("^/category/(?<id>(\\d+))/$");
                m = p.matcher(url);
                if (m.find()) {
                    categorySerializer = new CategorySerializer(request.getBody());
                    categoryService.update(categorySerializer.toObject());
                    response.setStatusCode(204);
                    response.setVersion("HTTP/1.1");
                    response.setStatusText("Нет данных");
                } else {
                    response.setStatusCode(400);
                    response.setVersion("HTTP/1.1");
                    response.setStatusText("Некорректный запрос");
                }
                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                break;
            case "DELETE":
                p = Pattern.compile("^/category/(?<id>(\\d+))/$");
                m = p.matcher(url);
                if (m.find()) {

                } else {
                    response.setStatusCode(400);
                    response.setVersion("HTTP/1.1");
                    response.setStatusText("Некорректный запрос");
                }
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
