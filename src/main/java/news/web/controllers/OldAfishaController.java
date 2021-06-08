package news.web.controllers;

import news.dao.specifications.FindAllAfishaSpecification;
import news.dao.specifications.FindByIdAfishaSpecification;
import news.dao.specifications.FindByTitleAfishaSpecification;
import news.dto.AfishaSerializer;
import news.model.Afisha;
import news.service.AfishaService;
import news.web.http.HttpRequest;
import news.web.http.HttpResponse;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
public class OldAfishaController implements Controller {
    HttpResponse response = new HttpResponse();
    AfishaService afishaService;
    AfishaSerializer afishaSerializer;

    public OldAfishaController(AfishaService afishaService) {
        this.afishaService = afishaService;
    }

    @Override
    public void buildResponse(HttpRequest request) throws SQLException {
        String fullUrl = request.getPath(true);
        String url = request.getPath(false);
        Pattern p;
        Matcher m;

        switch (request.getMethod()) {
            case ("GET"): {
                p = Pattern.compile("^/afisha/$");
                m = p.matcher(fullUrl);
                System.out.println(fullUrl);
                // получение списка всех афиш
                if (m.find()) {
                    FindAllAfishaSpecification findAll = new FindAllAfishaSpecification();
                    List<Afisha> findAllAfishaList = afishaService.query(findAll);
                    if (findAllAfishaList.isEmpty()) {
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
                        for (int i = 0; i < findAllAfishaList.size(); i++) {
                            afishaSerializer = new AfishaSerializer(findAllAfishaList.get(i));
                            body.append(afishaSerializer.toJSON());
                            if (i != findAllAfishaList.size() - 1) {
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
                // получение списка афиш отобранных по параметру title
                p = Pattern.compile("^/afisha\\?title=(?<title>([\\w@\\d.]+))$", Pattern.UNICODE_CHARACTER_CLASS);
                m = p.matcher(fullUrl);
                if (m.find()) {
                    FindByTitleAfishaSpecification findByTitle = new FindByTitleAfishaSpecification(m.group("title"));
                    List<Afisha> findByTitleAfishaList = afishaService.query(findByTitle);
                    if (findByTitleAfishaList.isEmpty()) {
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
                        for (int i = 0; i < findByTitleAfishaList.size(); i++) {
                            afishaSerializer = new AfishaSerializer(findByTitleAfishaList.get(i));
                            body.append(afishaSerializer.toJSON());
                            if (i != findByTitleAfishaList.size() - 1) {
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
                // получение афиши по id
                p = Pattern.compile("^/afisha/(?<id>(\\d+))/$");
                m = p.matcher(fullUrl);
                if (m.find()) {
                    FindByIdAfishaSpecification findById = new FindByIdAfishaSpecification(Integer.parseInt(m.group("id")));
                    List<Afisha> findByIdAfishaList = afishaService.query(findById);
                    if (findByIdAfishaList.isEmpty()) {
                        response.setStatusCode(404);
                        response.setStatusText("Афиша не найдена");
                        response.setVersion("HTTP/1.1");
                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                        response.setHeader("Pragma", "no-cache");
                        response.setBody("[]");
                        break;
                    } else {
                        Afisha afisha = findByIdAfishaList.get(0);
                        afishaSerializer = new AfishaSerializer(afisha);
                        response.setStatusCode(200);
                        response.setStatusText("OK");
                        response.setVersion("HTTP/1.1");
                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                        response.setHeader("Pragma", "no-cache");
                        response.setBody(afishaSerializer.toJSON());
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
                p = Pattern.compile("^/afisha/$");
                m = p.matcher(url);
                if (m.find()) {
                    afishaSerializer = new AfishaSerializer(request.getBody());
                    int id = afishaService.create(afishaSerializer.toObject());
                    response.setStatusCode(201);
                    response.setStatusText("Афиша создана");
                    response.setVersion("HTTP/1.1");
                    response.setHeader("Location", String.format("/afisha/%s/", id));
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
                p = Pattern.compile("^/afisha/(?<id>(\\d+))/$");
                m = p.matcher(url);
                // если status=0 - не было выполнено обновление, если не 0 - выполнено
                int statusUpdate;
                if (m.find()) {
                    afishaSerializer = new AfishaSerializer(request.getBody());
                    // нужно сравнить id из fullUrl и id из body. Если не совпадают то вернуть ответ с "Некорректный запрос"
                    Afisha afisha = afishaSerializer.toObject();
                    int idAfishaFromBody = (int) afisha.getObjects()[0];
                    if (Integer.parseInt(m.group("id")) != idAfishaFromBody) {
                        response.setStatusCode(400);
                        response.setVersion("HTTP/1.1");
                        response.setStatusText("Некорректный запрос");
                    } else {
                        statusUpdate = afishaService.update(afishaSerializer.toObject());
                        if (statusUpdate != 0) {
                            response.setStatusCode(204);
                            response.setStatusText("Нет данных");
                        } else {
                            response.setStatusCode(404);
                            response.setStatusText("Афиша для обновления не найдена");
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
                p = Pattern.compile("^/afisha/(?<id>(\\d+))/$");
                m = p.matcher(url);
                if (m.find()) {
                    int id = Integer.parseInt(m.group("id"));
                    int statusDelete;
                    statusDelete = afishaService.delete(id);
                    if (statusDelete != 0) {
                        response.setStatusCode(204);
                        response.setStatusText("Нет данных");
                    } else {
                        response.setStatusCode(404);
                        response.setStatusText("Афиша для удаления не найдена");
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
