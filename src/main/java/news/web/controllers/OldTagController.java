package news.web.controllers;

import news.dao.specifications.FindAllTagSpecification;
import news.dao.specifications.FindByIdTagSpecification;
import news.dao.specifications.FindByTitleTagSpecification;
import news.dto.TagSerializer;
import news.model.Tag;
import news.service.TagService;
import news.web.http.HttpRequest;
import news.web.http.HttpResponse;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
public class OldTagController implements Controller {
    HttpResponse response = new HttpResponse();
    TagService tagService;
    TagSerializer tagSerializer;

    public OldTagController(TagService tagService) {
        this.tagService = tagService;
    }

    @Override
    public void buildResponse(HttpRequest request) throws SQLException {
        String fullUrl = request.getPath(true);
        String url = request.getPath(false);
        Pattern p;
        Matcher m;

        switch (request.getMethod()) {
            case ("GET"): {
                p = Pattern.compile("^/tag/$");
                m = p.matcher(fullUrl);
                // получение списка всех тегов
                if (m.find()) {
                    FindAllTagSpecification findAll = new FindAllTagSpecification();
                    List<Tag> findAllTagList = tagService.query(findAll);
                    if (findAllTagList.isEmpty()) {
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
                        for (int i = 0; i < findAllTagList.size(); i++) {
                            tagSerializer = new TagSerializer(findAllTagList.get(i));
                            body.append(tagSerializer.toJSON());
                            if (i != findAllTagList.size() - 1) {
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
                // получение списка тегов отобранных по параметру title
                p = Pattern.compile("^/tag\\?title=(?<title>(\\w+))$", Pattern.UNICODE_CHARACTER_CLASS);
                m = p.matcher(fullUrl);
                if (m.find()) {
                    FindByTitleTagSpecification findByTitle = new FindByTitleTagSpecification(m.group("title"));
                    List<Tag> findByTitleTagList = tagService.query(findByTitle);
                    if (findByTitleTagList.isEmpty()) {
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
                        for (int i = 0; i < findByTitleTagList.size(); i++) {
                            tagSerializer = new TagSerializer(findByTitleTagList.get(i));
                            body.append(tagSerializer.toJSON());
                            if (i != findByTitleTagList.size() - 1) {
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
                // получение тега по id
                p = Pattern.compile("^/tag/(?<id>(\\d+))/$");
                m = p.matcher(fullUrl);
                if (m.find()) {
                    FindByIdTagSpecification findById = new FindByIdTagSpecification(Integer.parseInt(m.group("id")));
                    List<Tag> findByIdTagList = tagService.query(findById);
                    if (findByIdTagList.isEmpty()) {
                        response.setStatusCode(404);
                        response.setStatusText("Тег не найден");
                        response.setVersion("HTTP/1.1");
                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                        response.setHeader("Pragma", "no-cache");
                        response.setBody("[]");
                        break;
                    } else {
                        Tag tag = findByIdTagList.get(0);
                        tagSerializer = new TagSerializer(tag);
                        response.setStatusCode(200);
                        response.setStatusText("OK");
                        response.setVersion("HTTP/1.1");
                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                        response.setHeader("Pragma", "no-cache");
                        response.setBody(tagSerializer.toJSON());
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
                p = Pattern.compile("^/tag/$");
                m = p.matcher(url);
                if (m.find()) {
                    tagSerializer = new TagSerializer(request.getBody());
                    int id = tagService.create(tagSerializer.toObject());
                    response.setStatusCode(201);
                    response.setStatusText("Тег создан");
                    response.setVersion("HTTP/1.1");
                    response.setHeader("Location", String.format("/tag/%s/", id));
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
                p = Pattern.compile("^/tag/(?<id>(\\d+))/$");
                m = p.matcher(url);
                // если status=0 - не было выполнено обновление, если не 0 - выполнено
                int statusUpdate;
                if (m.find()) {
                    tagSerializer = new TagSerializer(request.getBody());
                    // нужно сравнить id из fullUrl и id из body. Если не совпадают то вернуть ответ с "Некорректный запрос"
                    Tag tag = tagSerializer.toObject();
                    int idTagFromBody = (int) tag.getObjects()[0];
                    if (Integer.parseInt(m.group("id")) != idTagFromBody) {
                        response.setStatusCode(400);
                        response.setVersion("HTTP/1.1");
                        response.setStatusText("Некорректный запрос");
                    } else {
                        statusUpdate = tagService.update(tagSerializer.toObject());
                        if (statusUpdate != 0) {
                            response.setStatusCode(204);
                            response.setStatusText("Нет данных");
                        } else {
                            response.setStatusCode(404);
                            response.setStatusText("Тег для обновления не найден");
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
                p = Pattern.compile("^/tag/(?<id>(\\d+))/$");
                m = p.matcher(url);
                if (m.find()) {
                    int id = Integer.parseInt(m.group("id"));
                    int statusDelete;
                    statusDelete = tagService.delete(id);
                    if (statusDelete != 0) {
                        response.setStatusCode(204);
                        response.setStatusText("Нет данных");
                    } else {
                        response.setStatusCode(404);
                        response.setStatusText("Тег для удаления не найден");
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
