//package news.web.controllers;
//
//import news.di.container.BeanFactory;
//import news.web.http.HttpRequest;
//import news.web.http.HttpResponse;
//
//import java.lang.reflect.InvocationTargetException;
//import java.sql.SQLException;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class RootController {
//    private HttpRequest request;
//    //final private ConnectionPool dbPool;
//    private HttpResponse response;
//
//    private AfishaController afishaController;
//    private ArticleController articleController;
//    private CategoryController categoryController;
//    private CommentController commentController;
//    private GroupController groupController;
//    private MailingController mailingController;
//    private SourceController sourceController;
//    private TagController tagController;
//    private UserController userController;
//
//    public RootController(HttpRequest request/*, ConnectionPool dbPool*/) {
//        this.request = request;
//        //this.dbPool = dbPool;
//        this.response = new HttpResponse();
//    }
//
//    public HttpResponse getResponse() throws SQLException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//        String url = request.getPath(false);
//        BeanFactory.setSettings(/*dbPool, */request, "src/main/resources/applicationContext.xml");
//        BeanFactory beanFactory = BeanFactory.getInstance();
//        Pattern p = Pattern.compile("/(.+?)/");
//        Matcher m = p.matcher(url);
//        if (m.find()) {
//            switch (m.group(1)) {
//                case ("article"): {
//                    articleController = beanFactory.getBean(ArticleController.class);
//                    if (articleController != null) {
//                        articleController.buildResponse();
//                        response = articleController.getResponse();
//                    } else {
//                        response.setStatusCode(503);
//                        response.setVersion("HTTP/1.1");
//                        response.setStatusText("Сервис по работе с article недоступен");
//                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                        response.setHeader("Pragma", "no-cache");
//                    }
//                    break;
//                }
//                case ("category"): {
//                    categoryController = beanFactory.getBean(CategoryController.class);
//                    if (categoryController != null) {
//                        categoryController.buildResponse();
//                        response = categoryController.getResponse();
//                    } else {
//                        response.setStatusCode(503);
//                        response.setVersion("HTTP/1.1");
//                        response.setStatusText("Сервис по работе с category недоступен");
//                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                        response.setHeader("Pragma", "no-cache");
//                    }
//                    break;
//                }
//                case ("group"): {
//                    groupController = beanFactory.getBean(GroupController.class);
//                    if (groupController != null) {
//                        groupController.buildResponse();
//                        response = groupController.getResponse();
//                    } else {
//                        response.setStatusCode(503);
//                        response.setVersion("HTTP/1.1");
//                        response.setStatusText("Сервис по работе с group недоступен");
//                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                        response.setHeader("Pragma", "no-cache");
//                    }
//                    break;
//                }
//                case ("source"): {
//                    sourceController = beanFactory.getBean(SourceController.class);
//                    if (sourceController != null) {
//                        sourceController.buildResponse();
//                        response = sourceController.getResponse();
//                    } else {
//                        response.setStatusCode(503);
//                        response.setVersion("HTTP/1.1");
//                        response.setStatusText("Сервис по работе с source недоступен");
//                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                        response.setHeader("Pragma", "no-cache");
//                    }
//                    break;
//                }
//                case ("mailing"): {
//                    mailingController = beanFactory.getBean(MailingController.class);
//                    if (mailingController != null) {
//                        mailingController.buildResponse();
//                        response = mailingController.getResponse();
//                    } else {
//                        response.setStatusCode(503);
//                        response.setVersion("HTTP/1.1");
//                        response.setStatusText("Сервис по работе с mailing недоступен");
//                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                        response.setHeader("Pragma", "no-cache");
//                    }
//                    break;
//                }
//                case ("tag"): {
//                    tagController = beanFactory.getBean(TagController.class);
//                    if (tagController != null) {
//                        tagController.buildResponse();
//                        response = tagController.getResponse();
//                    } else {
//                        response.setStatusCode(503);
//                        response.setVersion("HTTP/1.1");
//                        response.setStatusText("Сервис по работе с tag недоступен");
//                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                        response.setHeader("Pragma", "no-cache");
//                    }
//                    break;
//                }
//                case ("user"): {
//                    userController = beanFactory.getBean(UserController.class);
//                    if (userController != null) {
//                        userController.buildResponse();
//                        response = userController.getResponse();
//                    } else {
//                        response.setStatusCode(503);
//                        response.setVersion("HTTP/1.1");
//                        response.setStatusText("Сервис по работе с user недоступен");
//                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                        response.setHeader("Pragma", "no-cache");
//                    }
//                    break;
//                }
//                case ("afisha"): {
//                    afishaController = beanFactory.getBean(AfishaController.class);
//                    if (afishaController != null) {
//                        afishaController.buildResponse();
//                        response = afishaController.getResponse();
//                    } else {
//                        response.setStatusCode(503);
//                        response.setVersion("HTTP/1.1");
//                        response.setStatusText("Сервис по работе с afisha недоступен");
//                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                        response.setHeader("Pragma", "no-cache");
//                    }
//                    break;
//                }
//                case ("comment"): {
//                    commentController = beanFactory.getBean(CommentController.class);
//                    if (commentController != null) {
//                        commentController.buildResponse();
//                        response = commentController.getResponse();
//                    } else {
//                        response.setStatusCode(503);
//                        response.setVersion("HTTP/1.1");
//                        response.setStatusText("Сервис по работе с comment недоступен");
//                        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                        response.setHeader("Pragma", "no-cache");
//                    }
//                    break;
//                }
//                default: {
//                    response.setStatusCode(400);
//                    response.setVersion("HTTP/1.1");
//                    response.setStatusText("Некорректный запрос");
//                    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                    response.setHeader("Pragma", "no-cache");
//                }
//            }
//        } else {
//            response.setStatusCode(404);
//            response.setVersion("HTTP/1.1");
//            response.setStatusText("Не найдено");
//        }
//
//        return response;
//    }
//}
