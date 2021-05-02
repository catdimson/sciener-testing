package news.web.controllers;

import news.dao.connection.DBPool;
import news.dao.repositories.*;
import news.service.*;
import news.web.http.HttpRequest;
import news.web.http.HttpResponse;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RootController {
    HttpRequest request;
    HttpResponse response = new HttpResponse();
    DBPool dbPool;

    // afisha
    AfishaRepository afishaRepository;
    AfishaService afishaService;
    AfishaController afishaController;
    // article
    ArticleRepository articleRepository;
    ArticleService articleService;
    ArticleController articleController;
    // category
    CategoryRepository categoryRepository;
    CategoryService categoryService;
    CategoryController categoryController;
    // comment
    CommentRepository commentRepository;
    CommentService commentService;
    CommentController commentController;
    // group
    GroupRepository groupRepository;
    GroupService groupService;
    GroupController groupController;
    // mailing
    MailingRepository mailingRepository;
    MailingService mailingService;
    MailingController mailingController;
    // source
    SourceRepository sourceRepository;
    SourceService sourceService;
    SourceController sourceController;
    // tag
    TagRepository tagRepository;
    TagService tagService;
    TagController tagController;
    // user
    UserRepository userRepository;
    UserService userService;
    UserController userController;

    public RootController(HttpRequest request, DBPool dbPool) {
        this.request = request;
        this.dbPool = dbPool;
    }

    public HttpResponse getResponse() throws SQLException {
        String url = request.getPath(false);
        System.out.println("RootController: fullUrl - " + request.getPath(true));
        System.out.println("RootController: url - " + request.getPath(false));
        Pattern p = Pattern.compile("/(.+?)/");
        Matcher m = p.matcher(url);
        if (m.find()) {
            switch (m.group(1)) {
                case ("article") -> {
                    articleRepository = new ArticleRepository(dbPool);
                    articleService = new ArticleService(articleRepository);
                    articleController = new ArticleController(articleService, request);
                    articleController.buildResponse();
                    response = articleController.getResponse();
                    break;
                }
                case ("category") -> {
                    categoryRepository = new CategoryRepository(dbPool);
                    categoryService = new CategoryService(categoryRepository);
                    categoryController = new CategoryController(categoryService, request);
                    categoryController.buildResponse();
                    response = categoryController.getResponse();
                    break;
                }
                case ("group") -> {
                    groupRepository = new GroupRepository(dbPool);
                    groupService = new GroupService(groupRepository);
                    groupController = new GroupController(groupService, request);
                    groupController.buildResponse();
                    response = groupController.getResponse();
                    break;
                }
                case ("source") -> {
                    sourceRepository = new SourceRepository(dbPool);
                    sourceService = new SourceService(sourceRepository);
                    sourceController = new SourceController(sourceService, request);
                    sourceController.buildResponse();
                    response = sourceController.getResponse();
                    break;
                }
                case ("mailing") -> {
                    mailingRepository = new MailingRepository(dbPool);
                    mailingService = new MailingService(mailingRepository);
                    mailingController = new MailingController(mailingService, request);
                    mailingController.buildResponse();
                    response = mailingController.getResponse();
                    break;
                }
                case ("tag") -> {
                    tagRepository = new TagRepository(dbPool);
                    tagService = new TagService(tagRepository);
                    tagController = new TagController(tagService, request);
                    tagController.buildResponse();
                    response = tagController.getResponse();
                    break;
                }
                case ("user") -> {
                    userRepository = new UserRepository(dbPool);
                    userService = new UserService(userRepository);
                    userController = new UserController(userService, request);
                    userController.buildResponse();
                    response = userController.getResponse();
                    break;
                }
                case ("afisha") -> {
                    afishaRepository = new AfishaRepository(dbPool);
                    afishaService = new AfishaService(afishaRepository);
                    afishaController = new AfishaController(afishaService, request);
                    afishaController.buildResponse();
                    response = afishaController.getResponse();
                    break;
                }
                case ("comment") -> {
                    commentRepository = new CommentRepository(dbPool);
                    commentService = new CommentService(commentRepository);
                    commentController = new CommentController(commentService, request);
                    commentController.buildResponse();
                    response = commentController.getResponse();
                    break;
                }
                default -> {
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
