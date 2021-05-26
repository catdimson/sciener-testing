package news.web.controllers;

import news.dao.connection.ConnectionPool;
import news.di.container.BeanFactory;
import news.web.http.HttpRequest;
import news.web.http.HttpResponse;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RootController {
    private HttpRequest request;
    final private ConnectionPool dbPool;
    private HttpResponse response;

    private AfishaController afishaController;
    private ArticleController articleController;
    private CategoryController categoryController;
    private CommentController commentController;
    private GroupController groupController;
    private MailingController mailingController;
    private SourceController sourceController;
    private TagController tagController;
    private UserController userController;

    public RootController(HttpRequest request, ConnectionPool dbPool) {
        this.request = request;
        this.dbPool = dbPool;
        this.response = new HttpResponse();
    }

    public HttpResponse getResponse() throws SQLException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String url = request.getPath(false);
        BeanFactory.setSettings(dbPool, request, "src/main/resources/applicationContext.xml");
        BeanFactory beanFactory = BeanFactory.getInstance();
        Pattern p = Pattern.compile("/(.+?)/");
        Matcher m = p.matcher(url);
        if (m.find()) {
            switch (m.group(1)) {
                case ("article"): {
                    articleController = beanFactory.getBean(ArticleController.class);
                    articleController.buildResponse();
                    response = articleController.getResponse();
                    break;
                }
                case ("category"): {
                    categoryController = beanFactory.getBean(CategoryController.class);
                    categoryController.buildResponse();
                    response = categoryController.getResponse();
                    break;
                }
                case ("group"): {
                    groupController = beanFactory.getBean(GroupController.class);
                    groupController.buildResponse();
                    response = groupController.getResponse();
                    break;
                }
                case ("source"): {
                    sourceController = beanFactory.getBean(SourceController.class);
                    sourceController.buildResponse();
                    response = sourceController.getResponse();
                    break;
                }
                case ("mailing"): {
                    mailingController = beanFactory.getBean(MailingController.class);
                    mailingController.buildResponse();
                    response = mailingController.getResponse();
                    break;
                }
                case ("tag"): {
                    tagController = beanFactory.getBean(TagController.class);
                    tagController.buildResponse();
                    response = tagController.getResponse();
                    break;
                }
                case ("user"): {
                    userController = beanFactory.getBean(UserController.class);
                    userController.buildResponse();
                    response = userController.getResponse();
                    break;
                }
                case ("afisha"): {
                    afishaController = beanFactory.getBean(AfishaController.class);
                    afishaController.buildResponse();
                    response = afishaController.getResponse();
                    break;
                }
                case ("comment"): {
                    commentController = beanFactory.getBean(CommentController.class);
                    commentController.buildResponse();
                    response = commentController.getResponse();
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
        } else {
            response.setStatusCode(404);
            response.setVersion("HTTP/1.1");
            response.setStatusText("Не найдено");
        }

        return response;
    }
}
