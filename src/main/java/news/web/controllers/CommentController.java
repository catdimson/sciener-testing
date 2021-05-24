package news.web.controllers;

import news.dao.specifications.FindAllCommentSpecification;
import news.dao.specifications.FindByIdCommentSpecification;
import news.dao.specifications.FindByUserIdCommentSpecification;
import news.dto.CommentSerializer;
import news.model.Comment;
import news.service.CommentService;
import news.web.http.HttpRequest;
import news.web.http.HttpResponse;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentController implements Controller {
    HttpRequest request;
    HttpResponse response = new HttpResponse();
    CommentService commentService;
    CommentSerializer commentSerializer;

    public CommentController(CommentService commentService, HttpRequest request) {
        this.commentService = commentService;
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
                p = Pattern.compile("^/comment/$");
                m = p.matcher(fullUrl);
                // получение списка всех комментариев
                if (m.find()) {
                    FindAllCommentSpecification findAll = new FindAllCommentSpecification();
                    List<Comment> findAllCommentList = commentService.query(findAll);
                    if (findAllCommentList.isEmpty()) {
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
                        for (int i = 0; i < findAllCommentList.size(); i++) {
                            commentSerializer = new CommentSerializer(findAllCommentList.get(i));
                            body.append(commentSerializer.toJSON());
                            if (i != findAllCommentList.size() - 1) {
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
                // получение списка комментариев отобранных по параметру title
                p = Pattern.compile("^/comment\\?userid=(?<userid>(\\d+))$", Pattern.UNICODE_CHARACTER_CLASS);
                m = p.matcher(fullUrl);
                if (m.find()) {
                    FindByUserIdCommentSpecification findByUserId = new FindByUserIdCommentSpecification(Integer.parseInt(m.group("userid")));
                    List<Comment> findByUserIdCommentList = commentService.query(findByUserId);
                    if (findByUserIdCommentList.isEmpty()) {
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
                        for (int i = 0; i < findByUserIdCommentList.size(); i++) {
                            commentSerializer = new CommentSerializer(findByUserIdCommentList.get(i));
                            body.append(commentSerializer.toJSON());
                            if (i != findByUserIdCommentList.size() - 1) {
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
                // получение комментарии по id
                p = Pattern.compile("^/comment/(?<id>(\\d+))/$");
                m = p.matcher(fullUrl);
                if (m.find()) {
                    FindByIdCommentSpecification findById = new FindByIdCommentSpecification(Integer.parseInt(m.group("id")));
                    List<Comment> findByIdCommentList = commentService.query(findById);
                    if (findByIdCommentList.isEmpty()) {
                        response.setStatusCode(404);
                        response.setStatusText("Комментарий не найден");
                        response.setVersion("HTTP/1.1");
                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                        response.setHeader("Pragma", "no-cache");
                        response.setBody("[]");
                        break;
                    } else {
                        Comment comment = findByIdCommentList.get(0);
                        commentSerializer = new CommentSerializer(comment);
                        response.setStatusCode(200);
                        response.setStatusText("OK");
                        response.setVersion("HTTP/1.1");
                        response.setHeader("Content-Type", "application/json; charset=UTF-8");
                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                        response.setHeader("Pragma", "no-cache");
                        response.setBody(commentSerializer.toJSON());
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
                p = Pattern.compile("^/comment/$");
                m = p.matcher(url);
                if (m.find()) {
                    commentSerializer = new CommentSerializer(request.getBody());
                    int id = commentService.create(commentSerializer.toObject());
                    response.setStatusCode(201);
                    response.setStatusText("Комментарий создан");
                    response.setVersion("HTTP/1.1");
                    response.setHeader("Location", String.format("/comment/%s/", id));
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
                p = Pattern.compile("^/comment/(?<id>(\\d+))/$");
                m = p.matcher(url);
                // если status=0 - не было выполнено обновление, если не 0 - выполнено
                int statusUpdate;
                if (m.find()) {
                    commentSerializer = new CommentSerializer(request.getBody());
                    // нужно сравнить id из fullUrl и id из body. Если не совпадают то вернуть ответ с "Некорректный запрос"
                    Comment comment = commentSerializer.toObject();
                    int idCommentFromBody = (int) comment.getObjects()[0];
                    if (Integer.parseInt(m.group("id")) != idCommentFromBody) {
                        response.setStatusCode(400);
                        response.setVersion("HTTP/1.1");
                        response.setStatusText("Некорректный запрос");
                    } else {
                        statusUpdate = commentService.update(commentSerializer.toObject());
                        if (statusUpdate != 0) {
                            response.setStatusCode(204);
                            response.setStatusText("Нет данных");
                        } else {
                            response.setStatusCode(404);
                            response.setStatusText("Комментарий для обновления не найден");
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
                p = Pattern.compile("^/comment/(?<id>(\\d+))/$");
                m = p.matcher(url);
                if (m.find()) {
                    int id = Integer.parseInt(m.group("id"));
                    int statusDelete;
                    statusDelete = commentService.delete(id);
                    if (statusDelete != 0) {
                        response.setStatusCode(204);
                        response.setStatusText("Нет данных");
                    } else {
                        response.setStatusCode(404);
                        response.setStatusText("Комментарий для удаления не найден");
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
