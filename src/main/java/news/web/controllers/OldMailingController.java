//package news.web.controllers;
//
//import news.dao.specifications.FindAllMailingSpecification;
//import news.dao.specifications.FindByIdMailingSpecification;
//import news.dao.specifications.FindByEmailMailingSpecification;
//import news.dto.MailingSerializer;
//import news.model.Mailing;
//import news.service.MailingService;
//import news.web.http.HttpRequest;
//import news.web.http.HttpResponse;
//
//import java.sql.SQLException;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Deprecated
//public class OldMailingController implements Controller {
//    HttpResponse response = new HttpResponse();
//    MailingService mailingService;
//    MailingSerializer mailingSerializer;
//
//    public OldMailingController(MailingService mailingService) {
//        this.mailingService = mailingService;
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
//                p = Pattern.compile("^/mailing/$");
//                m = p.matcher(fullUrl);
//                // получение списка всех рассылок
//                if (m.find()) {
//                    FindAllMailingSpecification findAll = new FindAllMailingSpecification();
//                    List<Mailing> findAllMailingList = mailingService.query(findAll);
//                    if (findAllMailingList.isEmpty()) {
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
//                        for (int i = 0; i < findAllMailingList.size(); i++) {
//                            mailingSerializer = new MailingSerializer(findAllMailingList.get(i));
//                            body.append(mailingSerializer.toJSON());
//                            if (i != findAllMailingList.size() - 1) {
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
//                // получение списка рассылок отобранных по параметру title
//                p = Pattern.compile("^/mailing\\?email=(?<title>([\\w\\d.]+@\\w+.\\w+))$", Pattern.UNICODE_CHARACTER_CLASS);
//                m = p.matcher(fullUrl);
//                if (m.find()) {
//                    FindByEmailMailingSpecification findByEmail = new FindByEmailMailingSpecification(m.group("title"));
//                    List<Mailing> findByEmailMailingList = mailingService.query(findByEmail);
//                    if (findByEmailMailingList.isEmpty()) {
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
//                        for (int i = 0; i < findByEmailMailingList.size(); i++) {
//                            mailingSerializer = new MailingSerializer(findByEmailMailingList.get(i));
//                            body.append(mailingSerializer.toJSON());
//                            if (i != findByEmailMailingList.size() - 1) {
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
//                // получение рассылки по id
//                p = Pattern.compile("^/mailing/(?<id>(\\d+))/$");
//                m = p.matcher(fullUrl);
//                if (m.find()) {
//                    FindByIdMailingSpecification findById = new FindByIdMailingSpecification(Integer.parseInt(m.group("id")));
//                    List<Mailing> findByIdMailingList = mailingService.query(findById);
//                    if (findByIdMailingList.isEmpty()) {
//                        response.setStatusCode(404);
//                        response.setStatusText("Рассылка не найдена");
//                        response.setVersion("HTTP/1.1");
//                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
//                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                        response.setHeader("Pragma", "no-cache");
//                        response.setBody("[]");
//                        break;
//                    } else {
//                        Mailing mailing = findByIdMailingList.get(0);
//                        mailingSerializer = new MailingSerializer(mailing);
//                        response.setStatusCode(200);
//                        response.setStatusText("OK");
//                        response.setVersion("HTTP/1.1");
//                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
//                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                        response.setHeader("Pragma", "no-cache");
//                        response.setBody(mailingSerializer.toJSON());
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
//                p = Pattern.compile("^/mailing/$");
//                m = p.matcher(url);
//                if (m.find()) {
//                    mailingSerializer = new MailingSerializer(request.getBody());
//                    int id = mailingService.create(mailingSerializer.toObject());
//                    response.setStatusCode(201);
//                    response.setStatusText("Рассылка создана");
//                    response.setVersion("HTTP/1.1");
//                    response.setHeader("Location", String.format("/mailing/%s/", id));
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
//                p = Pattern.compile("^/mailing/(?<id>(\\d+))/$");
//                m = p.matcher(url);
//                // если status=0 - не было выполнено обновление, если не 0 - выполнено
//                int statusUpdate;
//                if (m.find()) {
//                    mailingSerializer = new MailingSerializer(request.getBody());
//                    // нужно сравнить id из fullUrl и id из body. Если не совпадают то вернуть ответ с "Некорректный запрос"
//                    Mailing mailing = mailingSerializer.toObject();
//                    int idMailingFromBody = (int) mailing.getObjects()[0];
//                    if (Integer.parseInt(m.group("id")) != idMailingFromBody) {
//                        response.setStatusCode(400);
//                        response.setVersion("HTTP/1.1");
//                        response.setStatusText("Некорректный запрос");
//                    } else {
//                        statusUpdate = mailingService.update(mailingSerializer.toObject());
//                        if (statusUpdate != 0) {
//                            response.setStatusCode(204);
//                            response.setStatusText("Нет данных");
//                        } else {
//                            response.setStatusCode(404);
//                            response.setStatusText("Рассылка для обновления не найдена");
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
//                p = Pattern.compile("^/mailing/(?<id>(\\d+))/$");
//                m = p.matcher(url);
//                if (m.find()) {
//                    int id = Integer.parseInt(m.group("id"));
//                    int statusDelete;
//                    statusDelete = mailingService.delete(id);
//                    if (statusDelete != 0) {
//                        response.setStatusCode(204);
//                        response.setStatusText("Нет данных");
//                    } else {
//                        response.setStatusCode(404);
//                        response.setStatusText("Рассылка для удаления не найдена");
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
