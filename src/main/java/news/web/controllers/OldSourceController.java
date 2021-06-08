//package news.web.controllers;
//
//import news.dao.specifications.FindAllSourceSpecification;
//import news.dao.specifications.FindByIdSourceSpecification;
//import news.dao.specifications.FindByTitleSourceSpecification;
//import news.dto.SourceSerializer;
//import news.model.Source;
//import news.service.SourceService;
//import news.web.http.HttpRequest;
//import news.web.http.HttpResponse;
//
//import java.sql.SQLException;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Deprecated
//public class OldSourceController implements Controller {
//    HttpResponse response = new HttpResponse();
//    SourceService sourceService;
//    SourceSerializer sourceSerializer;
//
//    public OldSourceController(SourceService sourceService) {
//        this.sourceService = sourceService;
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
//                p = Pattern.compile("^/source/$");
//                m = p.matcher(fullUrl);
//                // получение списка всех источников
//                if (m.find()) {
//                    FindAllSourceSpecification findAll = new FindAllSourceSpecification();
//                    List<Source> findAllSourceList = sourceService.query(findAll);
//                    if (findAllSourceList.isEmpty()) {
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
//                        for (int i = 0; i < findAllSourceList.size(); i++) {
//                            sourceSerializer = new SourceSerializer(findAllSourceList.get(i));
//                            body.append(sourceSerializer.toJSON());
//                            if (i != findAllSourceList.size() - 1) {
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
//                // получение списка источников отобранных по параметру title
//                p = Pattern.compile("^/source\\?title=(?<title>(\\w+))$", Pattern.UNICODE_CHARACTER_CLASS);
//                m = p.matcher(fullUrl);
//                if (m.find()) {
//                    FindByTitleSourceSpecification findByTitle = new FindByTitleSourceSpecification(m.group("title"));
//                    List<Source> findByTitleSourceList = sourceService.query(findByTitle);
//                    if (findByTitleSourceList.isEmpty()) {
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
//                        for (int i = 0; i < findByTitleSourceList.size(); i++) {
//                            sourceSerializer = new SourceSerializer(findByTitleSourceList.get(i));
//                            body.append(sourceSerializer.toJSON());
//                            if (i != findByTitleSourceList.size() - 1) {
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
//                // получение источники по id
//                p = Pattern.compile("^/source/(?<id>(\\d+))/$");
//                m = p.matcher(fullUrl);
//                if (m.find()) {
//                    FindByIdSourceSpecification findById = new FindByIdSourceSpecification(Integer.parseInt(m.group("id")));
//                    List<Source> findByIdSourceList = sourceService.query(findById);
//                    if (findByIdSourceList.isEmpty()) {
//                        response.setStatusCode(404);
//                        response.setStatusText("Источник не найден");
//                        response.setVersion("HTTP/1.1");
//                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
//                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                        response.setHeader("Pragma", "no-cache");
//                        response.setBody("[]");
//                        break;
//                    } else {
//                        Source source = findByIdSourceList.get(0);
//                        sourceSerializer = new SourceSerializer(source);
//                        response.setStatusCode(200);
//                        response.setStatusText("OK");
//                        response.setVersion("HTTP/1.1");
//                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
//                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                        response.setHeader("Pragma", "no-cache");
//                        response.setBody(sourceSerializer.toJSON());
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
//                p = Pattern.compile("^/source/$");
//                m = p.matcher(url);
//                if (m.find()) {
//                    sourceSerializer = new SourceSerializer(request.getBody());
//                    int id = sourceService.create(sourceSerializer.toObject());
//                    response.setStatusCode(201);
//                    response.setStatusText("Источник создан");
//                    response.setVersion("HTTP/1.1");
//                    response.setHeader("Location", String.format("/source/%s/", id));
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
//                p = Pattern.compile("^/source/(?<id>(\\d+))/$");
//                m = p.matcher(url);
//                // если status=0 - не было выполнено обновление, если не 0 - выполнено
//                int statusUpdate;
//                if (m.find()) {
//                    sourceSerializer = new SourceSerializer(request.getBody());
//                    // нужно сравнить id из fullUrl и id из body. Если не совпадают то вернуть ответ с "Некорректный запрос"
//                    Source source = sourceSerializer.toObject();
//                    int idSourceFromBody = (int) source.getObjects()[0];
//                    if (Integer.parseInt(m.group("id")) != idSourceFromBody) {
//                        response.setStatusCode(400);
//                        response.setVersion("HTTP/1.1");
//                        response.setStatusText("Некорректный запрос");
//                    } else {
//                        statusUpdate = sourceService.update(sourceSerializer.toObject());
//                        if (statusUpdate != 0) {
//                            response.setStatusCode(204);
//                            response.setStatusText("Нет данных");
//                        } else {
//                            response.setStatusCode(404);
//                            response.setStatusText("Источник для обновления не найден");
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
//                p = Pattern.compile("^/source/(?<id>(\\d+))/$");
//                m = p.matcher(url);
//                if (m.find()) {
//                    int id = Integer.parseInt(m.group("id"));
//                    int statusDelete;
//                    statusDelete = sourceService.delete(id);
//                    if (statusDelete != 0) {
//                        response.setStatusCode(204);
//                        response.setStatusText("Нет данных");
//                    } else {
//                        response.setStatusCode(404);
//                        response.setStatusText("Источник для удаления не найден");
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
