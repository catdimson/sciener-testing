package news.web.controllers;

import news.dao.specifications.FindAllUserSpecification;
import news.dao.specifications.FindByIdUserSpecification;
import news.dao.specifications.FindByFirstnameUserSpecification;
import news.dto.UserSerializer;
import news.model.User;
import news.service.UserService;
import news.web.http.HttpRequest;
import news.web.http.HttpResponse;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserController implements Controller {
    HttpRequest request;
    HttpResponse response = new HttpResponse();
    UserService userService;
    UserSerializer userSerializer;

    public UserController(UserService userService, HttpRequest request) {
        this.userService = userService;
        this.request = request;
    }

    @Override
    public void buildResponse() throws SQLException {
        String fullUrl = request.getPath(true);
        String url = request.getPath(false);
        Pattern p;
        Matcher m;

        switch (request.getMethod()) {
            case ("GET"): {
                p = Pattern.compile("^/user/$");
                m = p.matcher(fullUrl);
                // получение списка всех пользователей
                if (m.find()) {
                    FindAllUserSpecification findAll = new FindAllUserSpecification();
                    List<User> findAllUserList = userService.query(findAll);
                    if (findAllUserList.isEmpty()) {
                        response.setStatusCode(200);
                        response.setStatusText("OK");
                        response.setVersion("HTTP/1.1");
                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                        response.setHeader("Pragma", "no-cache");
                        response.setBody("[]");
                        break;
                    } else {
                        response.setStatusCode(200);
                        response.setStatusText("OK");
                        response.setVersion("HTTP/1.1");
                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                        response.setHeader("Pragma", "no-cache");
                        StringBuilder body = new StringBuilder();
                        for (int i = 0; i < findAllUserList.size(); i++) {
                            userSerializer = new UserSerializer(findAllUserList.get(i));
                            body.append(userSerializer.toJSON());
                            if (i != findAllUserList.size() - 1) {
                                body.append(",\n");
                            } else {
                                body.append("\n");
                            }
                        }
                        body.insert(0, "[\n").append("]\n");
                        response.setBody(body.toString());
                        break;
                    }
                }
                // получение списка пользователей отобранных по параметру firstname
                p = Pattern.compile("^/user\\?firstname=(?<firstname>([\\w\\d]+))$", Pattern.UNICODE_CHARACTER_CLASS);
                m = p.matcher(fullUrl);
                if (m.find()) {
                    FindByFirstnameUserSpecification findByFirstname = new FindByFirstnameUserSpecification(m.group("firstname"));
                    List<User> findByFirstnameUserList = userService.query(findByFirstname);
                    if (findByFirstnameUserList.isEmpty()) {
                        response.setStatusCode(200);
                        response.setStatusText("OK");
                        response.setVersion("HTTP/1.1");
                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                        response.setHeader("Pragma", "no-cache");
                        response.setBody("[]");
                        break;
                    } else {
                        response.setStatusCode(200);
                        response.setStatusText("OK");
                        response.setVersion("HTTP/1.1");
                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                        response.setHeader("Pragma", "no-cache");
                        StringBuilder body = new StringBuilder();
                        for (int i = 0; i < findByFirstnameUserList.size(); i++) {
                            userSerializer = new UserSerializer(findByFirstnameUserList.get(i));
                            body.append(userSerializer.toJSON());
                            if (i != findByFirstnameUserList.size() - 1) {
                                body.append(",\n");
                            } else {
                                body.append("\n");
                            }
                        }
                        body.insert(0, "[\n").append("]\n");
                        response.setBody(body.toString());
                        break;
                    }
                }
                // получение пользователя по id
                p = Pattern.compile("^/user/(?<id>(\\d+))/$");
                m = p.matcher(fullUrl);
                if (m.find()) {
                    FindByIdUserSpecification findById = new FindByIdUserSpecification(Integer.parseInt(m.group("id")));
                    List<User> findByIdUserList = userService.query(findById);
                    if (findByIdUserList.isEmpty()) {
                        response.setStatusCode(404);
                        response.setStatusText("Пользователь не найден");
                        response.setVersion("HTTP/1.1");
                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                        response.setHeader("Pragma", "no-cache");
                        response.setBody("[]");
                        break;
                    } else {
                        User user = findByIdUserList.get(0);
                        userSerializer = new UserSerializer(user);
                        response.setStatusCode(200);
                        response.setStatusText("OK");
                        response.setVersion("HTTP/1.1");
                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                        response.setHeader("Pragma", "no-cache");
                        response.setBody(userSerializer.toJSON());
                        break;
                    }
                } else {
                    response.setStatusCode(400);
                    response.setVersion("HTTP/1.1");
                    response.setStatusText("Некорректный запрос");
                    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                    response.setHeader("Pragma", "no-cache");
                    break;
                }
            }
            case ("POST"): {
                // создание записи
                p = Pattern.compile("^/user/$");
                m = p.matcher(url);
                if (m.find()) {
                    userSerializer = new UserSerializer(request.getBody());
                    int id = userService.create(userSerializer.toObject());
                    response.setStatusCode(201);
                    response.setStatusText("Пользователь создан");
                    response.setVersion("HTTP/1.1");
                    response.setHeader("Location", String.format("/user/%s/", id));
                } else {
                    response.setStatusCode(400);
                    response.setVersion("HTTP/1.1");
                    response.setStatusText("Некорректный запрос");
                }
                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                break;
            }
            case ("PUT"): {
                p = Pattern.compile("^/user/(?<id>(\\d+))/$");
                m = p.matcher(url);
                // если status=0 - не было выполнено обновление, если не 0 - выполнено
                int statusUpdate;
                if (m.find()) {
                    userSerializer = new UserSerializer(request.getBody());
                    // нужно сравнить id из fullUrl и id из body. Если не совпадают то вернуть ответ с "Некорректный запрос"
                    User user = userSerializer.toObject();
                    int idUserFromBody = (int) user.getObjects()[0];
                    if (Integer.parseInt(m.group("id")) != idUserFromBody) {
                        response.setStatusCode(400);
                        response.setVersion("HTTP/1.1");
                        response.setStatusText("Некорректный запрос");
                    } else {
                        statusUpdate = userService.update(userSerializer.toObject());
                        if (statusUpdate != 0) {
                            response.setStatusCode(204);
                            response.setStatusText("Нет данных");
                        } else {
                            response.setStatusCode(404);
                            response.setStatusText("Пользователь для обновления не найден");
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
            }
            case ("DELETE"): {
                p = Pattern.compile("^/user/(?<id>(\\d+))/$");
                m = p.matcher(url);
                if (m.find()) {
                    int id = Integer.parseInt(m.group("id"));
                    int statusDelete;
                    statusDelete = userService.delete(id);
                    if (statusDelete != 0) {
                        response.setStatusCode(204);
                        response.setStatusText("Нет данных");
                    } else {
                        response.setStatusCode(404);
                        response.setStatusText("Пользователь для удаления не найден");
                    }
                } else {
                    response.setStatusCode(400);
                    response.setStatusText("Некорректный запрос");
                }
                response.setVersion("HTTP/1.1");
                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                break;
            }
            default: {
                response.setStatusCode(400);
                response.setVersion("HTTP/1.1");
                response.setStatusText("Некорректный запрос");
                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                response.setHeader("Pragma", "no-cache");
            }
        }
    }

    @Override
    public HttpResponse getResponse() {
        return response;
    }
}
