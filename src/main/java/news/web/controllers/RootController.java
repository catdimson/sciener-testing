package news.web.controllers;

import news.dao.connection.ConnectionPool;
import news.dao.repositories.*;
import news.di.container.BeanFactory;
import news.service.*;
import news.web.http.HttpRequest;
import news.web.http.HttpResponse;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RootController {
    final private HttpRequest request;
    final private ConnectionPool dbPool;
    private HttpResponse response;

    // afisha
    private AfishaRepository afishaRepository;
    private AfishaService afishaService;
    private AfishaController afishaController;
    // article
    private ArticleRepository articleRepository;
    private ArticleService articleService;
    private ArticleController articleController;
    // category
    private CategoryRepository categoryRepository;
    private CategoryService categoryService;
    private CategoryController categoryController;
    // comment
    private CommentRepository commentRepository;
    private CommentService commentService;
    private CommentController commentController;
    // group
    private GroupRepository groupRepository;
    private GroupService groupService;
    private GroupController groupController;
    // mailing
    private MailingRepository mailingRepository;
    private MailingService mailingService;
    private MailingController mailingController;
    // source
    private SourceRepository sourceRepository;
    private SourceService sourceService;
    private SourceController sourceController;
    // tag
    private TagRepository tagRepository;
    private TagService tagService;
    private TagController tagController;
    // user
    private UserRepository userRepository;
    private UserService userService;
    private UserController userController;

    public RootController(HttpRequest request, ConnectionPool dbPool) {
        this.request = request;
        this.dbPool = dbPool;
        this.response = new HttpResponse();
    }

    public HttpResponse getResponse() throws SQLException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String url = request.getPath(false);
        BeanFactory.setSettings(dbPool, request, "src/main/resources/applicationContext.xml");
        Pattern p = Pattern.compile("/(.+?)/");
        Matcher m = p.matcher(url);
        if (m.find()) {
            switch (m.group(1)) {
                case ("article"): {
                    articleRepository = new ArticleRepository(dbPool);
                    articleService = new ArticleService(articleRepository);
                    articleController = new ArticleController(articleService, request);
                    articleController.buildResponse();
                    response = articleController.getResponse();
                    break;
                }
                case ("category"): {
                    categoryRepository = new CategoryRepository(dbPool);
                    categoryService = new CategoryService(categoryRepository);
                    categoryController = new CategoryController(categoryService, request);
                    categoryController.buildResponse();
                    response = categoryController.getResponse();
                    break;
                }
                case ("group"): {
                    groupRepository = new GroupRepository(dbPool);
                    groupService = new GroupService(groupRepository);
                    groupController = new GroupController(groupService, request);
                    groupController.buildResponse();
                    response = groupController.getResponse();
                    break;
                }
                case ("source"): {
                    sourceRepository = new SourceRepository(dbPool);
                    sourceService = new SourceService(sourceRepository);
                    sourceController = new SourceController(sourceService, request);
                    sourceController.buildResponse();
                    response = sourceController.getResponse();
                    break;
                }
                case ("mailing"): {
                    mailingRepository = new MailingRepository(dbPool);
                    mailingService = new MailingService(mailingRepository);
                    mailingController = new MailingController(mailingService, request);
                    mailingController.buildResponse();
                    response = mailingController.getResponse();
                    break;
                }
                case ("tag"): {
                    tagController = BeanFactory.getInstance().getBean(TagController.class);
                    tagController.buildResponse();
                    response = tagController.getResponse();
                    break;

                }
                case ("user"): {
                    userRepository = new UserRepository(dbPool);
                    userService = new UserService(userRepository);
                    userController = new UserController(userService, request);
                    userController.buildResponse();
                    response = userController.getResponse();
                    break;
                }
                case ("afisha"): {
                    afishaRepository = new AfishaRepository(dbPool);
                    afishaService = new AfishaService(afishaRepository);
                    afishaController = new AfishaController(afishaService, request);
                    afishaController.buildResponse();
                    response = afishaController.getResponse();
                    break;
                }
                case ("comment"): {
                    commentRepository = new CommentRepository(dbPool);
                    commentService = new CommentService(commentRepository);
                    commentController = new CommentController(commentService, request);
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
