//package news.web.controllers;
//
//import news.dao.specifications.FindAllCategorySpecification;
//import news.dao.specifications.FindByIdCategorySpecification;
//import news.dao.specifications.FindByTitleCategorySpecification;
//import news.dto.CategorySerializer;
//import news.model.Category;
//import news.service.CategoryService;
//import news.web.http.HttpRequest;
//import news.web.http.HttpResponse;
//
//import java.sql.SQLException;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Deprecated
//public class OldCategoryController implements Controller {
//    HttpResponse response = new HttpResponse();
//    CategoryService categoryService;
//    CategorySerializer categorySerializer;
//
//    public OldCategoryController(CategoryService categoryService) {
//        this.categoryService = categoryService;
//    }
//
//    @Override
//    public void buildResponse(HttpRequest request) throws SQLException {
//        String fullUrl = request.getPath(true);
//        String url = request.getPath(false);
//        Pattern p;
//        Matcher m;
//
//        switch (request.getMethod()) {
//            case ("GET"): {
//                p = Pattern.compile("^/category/$");
//                m = p.matcher(fullUrl);
//                // получение списка всех категорий
//                if (m.find()) {
//                    FindAllCategorySpecification findAll = new FindAllCategorySpecification();
//                    List<Category> findAllCategoryList = categoryService.query(findAll);
//                    if (findAllCategoryList.isEmpty()) {
//                        response.setStatusCode(200);
//                        response.setStatusText("OK");
//                        response.setVersion("HTTP/1.1");
//                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
//                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                        response.setHeader("Pragma", "no-cache");
//                        response.setBody("[]");
//                        break;
//                    } else {
//                        response.setStatusCode(200);
//                        response.setStatusText("OK");
//                        response.setVersion("HTTP/1.1");
//                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
//                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                        response.setHeader("Pragma", "no-cache");
//                        StringBuilder body = new StringBuilder();
//                        for (int i = 0; i < findAllCategoryList.size(); i++) {
//                            categorySerializer = new CategorySerializer(findAllCategoryList.get(i));
//                            body.append(categorySerializer.toJSON());
//                            if (i != findAllCategoryList.size() - 1) {
//                                body.append(",\n");
//                            } else {
//                                body.append("\n");
//                            }
//                        }
//                        body.insert(0, "[\n").append("]\n");
//                        response.setBody(body.toString());
//                        break;
//                    }
//                }
//                // получение списка категорий отобранных по параметру title
//                p = Pattern.compile("^/category\\?title=(?<title>(\\w+))$", Pattern.UNICODE_CHARACTER_CLASS);
//                m = p.matcher(fullUrl);
//                if (m.find()) {
//                    FindByTitleCategorySpecification findByTitle = new FindByTitleCategorySpecification(m.group("title"));
//                    List<Category> findByTitleCategoryList = categoryService.query(findByTitle);
//                    if (findByTitleCategoryList.isEmpty()) {
//                        response.setStatusCode(200);
//                        response.setStatusText("OK");
//                        response.setVersion("HTTP/1.1");
//                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
//                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                        response.setHeader("Pragma", "no-cache");
//                        response.setBody("[]");
//                        break;
//                    } else {
//                        response.setStatusCode(200);
//                        response.setStatusText("OK");
//                        response.setVersion("HTTP/1.1");
//                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
//                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                        response.setHeader("Pragma", "no-cache");
//                        StringBuilder body = new StringBuilder();
//                        for (int i = 0; i < findByTitleCategoryList.size(); i++) {
//                            categorySerializer = new CategorySerializer(findByTitleCategoryList.get(i));
//                            body.append(categorySerializer.toJSON());
//                            if (i != findByTitleCategoryList.size() - 1) {
//                                body.append(",\n");
//                            } else {
//                                body.append("\n");
//                            }
//                        }
//                        body.insert(0, "[\n").append("]\n");
//                        response.setBody(body.toString());
//                        break;
//                    }
//                }
//                // получение категории по id
//                p = Pattern.compile("^/category/(?<id>(\\d+))/$");
//                m = p.matcher(fullUrl);
//                if (m.find()) {
//                    FindByIdCategorySpecification findById = new FindByIdCategorySpecification(Integer.parseInt(m.group("id")));
//                    List<Category> findByIdCategoryList = categoryService.query(findById);
//                    if (findByIdCategoryList.isEmpty()) {
//                        response.setStatusCode(404);
//                        response.setStatusText("Категория не найдена");
//                        response.setVersion("HTTP/1.1");
//                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
//                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                        response.setHeader("Pragma", "no-cache");
//                        response.setBody("[]");
//                        break;
//                    } else {
//                        Category category = findByIdCategoryList.get(0);
//                        categorySerializer = new CategorySerializer(category);
//                        response.setStatusCode(200);
//                        response.setStatusText("OK");
//                        response.setVersion("HTTP/1.1");
//                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
//                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                        response.setHeader("Pragma", "no-cache");
//                        response.setBody(categorySerializer.toJSON());
//                        break;
//                    }
//                } else {
//                    response.setStatusCode(400);
//                    response.setVersion("HTTP/1.1");
//                    response.setStatusText("Некорректный запрос");
//                    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                    response.setHeader("Pragma", "no-cache");
//                    break;
//                }
//            }
//            case ("POST"): {
//                // создание записи
//                p = Pattern.compile("^/category/$");
//                m = p.matcher(url);
//                if (m.find()) {
//                    categorySerializer = new CategorySerializer(request.getBody());
//                    int id = categoryService.create(categorySerializer.toObject());
//                    response.setStatusCode(201);
//                    response.setStatusText("Категория создана");
//                    response.setVersion("HTTP/1.1");
//                    response.setHeader("Location", String.format("/category/%s/", id));
//                } else {
//                    response.setStatusCode(400);
//                    response.setVersion("HTTP/1.1");
//                    response.setStatusText("Некорректный запрос");
//                }
//                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                response.setHeader("Pragma", "no-cache");
//                break;
//            }
//            case ("PUT"): {
//                p = Pattern.compile("^/category/(?<id>(\\d+))/$");
//                m = p.matcher(url);
//                // если status=0 - не было выполнено обновление, если не 0 - выполнено
//                int statusUpdate;
//                if (m.find()) {
//                    categorySerializer = new CategorySerializer(request.getBody());
//                    // нужно сравнить id из fullUrl и id из body. Если не совпадают то вернуть ответ с "Некорректный запрос"
//                    Category category = categorySerializer.toObject();
//                    int idCategoryFromBody = (int) category.getObjects()[0];
//                    if (Integer.parseInt(m.group("id")) != idCategoryFromBody) {
//                        response.setStatusCode(400);
//                        response.setVersion("HTTP/1.1");
//                        response.setStatusText("Некорректный запрос");
//                    } else {
//                        statusUpdate = categoryService.update(categorySerializer.toObject());
//                        if (statusUpdate != 0) {
//                            response.setStatusCode(204);
//                            response.setStatusText("Нет данных");
//                        } else {
//                            response.setStatusCode(404);
//                            response.setStatusText("Категория для обновления не найдена");
//                        }
//                    }
//                    response.setVersion("HTTP/1.1");
//                } else {
//                    response.setStatusCode(400);
//                    response.setVersion("HTTP/1.1");
//                    response.setStatusText("Некорректный запрос");
//                }
//                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                response.setHeader("Pragma", "no-cache");
//                break;
//            }
//            case ("DELETE"): {
//                p = Pattern.compile("^/category/(?<id>(\\d+))/$");
//                m = p.matcher(url);
//                if (m.find()) {
//                    int id = Integer.parseInt(m.group("id"));
//                    int statusDelete;
//                    statusDelete = categoryService.delete(id);
//                    if (statusDelete != 0) {
//                        response.setStatusCode(204);
//                        response.setStatusText("Нет данных");
//                    } else {
//                        response.setStatusCode(404);
//                        response.setStatusText("Категория для удаления не найдена");
//                    }
//                } else {
//                    response.setStatusCode(400);
//                    response.setStatusText("Некорректный запрос");
//                }
//                response.setVersion("HTTP/1.1");
//                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                response.setHeader("Pragma", "no-cache");
//                break;
//            }
//            default: {
//                response.setStatusCode(400);
//                response.setVersion("HTTP/1.1");
//                response.setStatusText("Некорректный запрос");
//                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                response.setHeader("Pragma", "no-cache");
//            }
//        }
//    }
//
//    @Override
//    public HttpResponse getResponse() {
//        return response;
//    }
//}
