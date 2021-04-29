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
    HttpResponse response;
    DBPool dbPool;

    // afisha
    AfishaRepository afishaRepository;
    AfishaService afishaService;
    AfishaController afishaController;
    // article
    ArticleRepository articleRepository;
    ArticleService articleService;
    // category
    CategoryRepository categoryRepository;
    CategoryService categoryService;
    CategoryController categoryController;
    // comment
    CommentRepository commentRepository;
    CommentService commentService;
    // group
    GroupRepository groupRepository;
    GroupService groupService;
    // mailing
    MailingRepository mailingRepository;
    MailingService mailingService;
    // source
    SourceRepository sourceRepository;
    SourceService sourceService;
    // tag
    TagRepository tagRepository;
    TagService tagService;
    // user
    UserRepository userRepository;
    UserService userService;

    public RootController(HttpRequest request, DBPool dbPool) {
        this.request = request;
        this.dbPool = dbPool;
    }

    public HttpResponse getResponse() throws SQLException {
        String url = request.getPath();
        Pattern p = Pattern.compile("/(.+)/");
        Matcher m = p.matcher(url);
        if (m.find()) {
            switch (m.group(1)) {
                case ("afisha"):
                    afishaRepository = new AfishaRepository(dbPool);
                    afishaService = new AfishaService(afishaRepository);
                    afishaController = new AfishaController(afishaService, request);
                    afishaController.buildResponse();
                    response = afishaController.getResponse();
                    break;
                /*case ("article"):
                    articleRepository = new ArticleRepository(dbPool);
                    articleService = new ArticleService(articleRepository);
                    articleController = new ArticleController(articleService);
                    // вызов метода у контроллера
                    response = articleController.getResponse();
                    break;*/
                case ("category"):
                    categoryRepository = new CategoryRepository(dbPool);
                    categoryService = new CategoryService(categoryRepository);
                    categoryController = new CategoryController(categoryService, request);
                    categoryController.buildResponse();
                    response = categoryController.getResponse();
                    break;
                default:
                    response.setStatusCode(400);
                    response.setVersion("HTTP/1.1");
                    response.setStatusText("Некорректный запрос");
            }
        } else {
            response.setStatusCode(404);
            response.setVersion("HTTP/1.1");
            response.setStatusText("Не найдено");
        }

        return response;
    }
}
