package news.web.controllers;

import news.dao.specifications.FindAllGroupSpecification;
import news.dao.specifications.FindByIdGroupSpecification;
import news.dao.specifications.FindByTitleGroupSpecification;
import news.dto.GroupSerializer;
import news.model.Group;
import news.service.GroupService;
import news.web.http.HttpRequest;
import news.web.http.HttpResponse;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupController implements Controller {
    HttpRequest request;
    HttpResponse response = new HttpResponse();
    GroupService groupService;
    GroupSerializer groupSerializer;

    public GroupController(GroupService groupService, HttpRequest request) {
        this.groupService = groupService;
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
                p = Pattern.compile("^/group/$");
                m = p.matcher(fullUrl);
                // получение списка всех групп
                if (m.find()) {
                    FindAllGroupSpecification findAll = new FindAllGroupSpecification();
                    List<Group> findAllGroupList = groupService.query(findAll);
                    if (findAllGroupList.isEmpty()) {
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
                        for (int i = 0; i < findAllGroupList.size(); i++) {
                            groupSerializer = new GroupSerializer(findAllGroupList.get(i));
                            body.append(groupSerializer.toJSON());
                            if (i != findAllGroupList.size() - 1) {
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
                // получение списка групп отобранных по параметру title
                p = Pattern.compile("^/group\\?title=(?<title>(\\w+))$", Pattern.UNICODE_CHARACTER_CLASS);
                m = p.matcher(fullUrl);
                if (m.find()) {
                    FindByTitleGroupSpecification findByTitle = new FindByTitleGroupSpecification(m.group("title"));
                    List<Group> findByTitleGroupList = groupService.query(findByTitle);
                    if (findByTitleGroupList.isEmpty()) {
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
                        for (int i = 0; i < findByTitleGroupList.size(); i++) {
                            groupSerializer = new GroupSerializer(findByTitleGroupList.get(i));
                            body.append(groupSerializer.toJSON());
                            if (i != findByTitleGroupList.size() - 1) {
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
                // получение группы по id
                p = Pattern.compile("^/group/(?<id>(\\d+))/$");
                m = p.matcher(fullUrl);
                if (m.find()) {
                    FindByIdGroupSpecification findById = new FindByIdGroupSpecification(Integer.parseInt(m.group("id")));
                    List<Group> findByIdGroupList = groupService.query(findById);
                    if (findByIdGroupList.isEmpty()) {
                        response.setStatusCode(404);
                        response.setStatusText("Группа не найдена");
                        response.setVersion("HTTP/1.1");
                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                        response.setHeader("Pragma", "no-cache");
                        response.setBody("[]");
                        break;
                    } else {
                        Group group = findByIdGroupList.get(0);
                        groupSerializer = new GroupSerializer(group);
                        response.setStatusCode(200);
                        response.setStatusText("OK");
                        response.setVersion("HTTP/1.1");
                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                        response.setHeader("Pragma", "no-cache");
                        response.setBody(groupSerializer.toJSON());
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
                p = Pattern.compile("^/group/$");
                m = p.matcher(url);
                if (m.find()) {
                    groupSerializer = new GroupSerializer(request.getBody());
                    int id = groupService.create(groupSerializer.toObject());
                    response.setStatusCode(201);
                    response.setStatusText("Группа создана");
                    response.setVersion("HTTP/1.1");
                    response.setHeader("Location", String.format("/group/%s/", id));
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
                p = Pattern.compile("^/group/(?<id>(\\d+))/$");
                m = p.matcher(url);
                // если status=0 - не было выполнено обновление, если не 0 - выполнено
                int statusUpdate;
                if (m.find()) {
                    groupSerializer = new GroupSerializer(request.getBody());
                    // нужно сравнить id из fullUrl и id из body. Если не совпадают то вернуть ответ с "Некорректный запрос"
                    Group group = groupSerializer.toObject();
                    int idGroupFromBody = (int) group.getObjects()[0];
                    if (Integer.parseInt(m.group("id")) != idGroupFromBody) {
                        response.setStatusCode(400);
                        response.setVersion("HTTP/1.1");
                        response.setStatusText("Некорректный запрос");
                    } else {
                        statusUpdate = groupService.update(groupSerializer.toObject());
                        if (statusUpdate != 0) {
                            response.setStatusCode(204);
                            response.setStatusText("Нет данных");
                        } else {
                            response.setStatusCode(404);
                            response.setStatusText("Группа для обновления не найдена");
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
                p = Pattern.compile("^/group/(?<id>(\\d+))/$");
                m = p.matcher(url);
                if (m.find()) {
                    int id = Integer.parseInt(m.group("id"));
                    int statusDelete;
                    statusDelete = groupService.delete(id);
                    if (statusDelete != 0) {
                        response.setStatusCode(204);
                        response.setStatusText("Нет данных");
                    } else {
                        response.setStatusCode(404);
                        response.setStatusText("Группа для удаления не найдена");
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
