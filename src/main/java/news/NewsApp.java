package news;

import news.dao.connection.ConnectionPool;
import news.dao.connection.DBPool;
import news.dao.repositories.*;
import news.service.*;
import news.web.http.HttpRequest;

import java.io.IOException;

public class NewsApp extends Thread {
    HttpRequest request;
    ConnectionPool dbPool;
    // afisha
    AfishaRepository afishaRepository;
    AfishaService afishaService;
    //AfishaController afishaController;
    // article
    ArticleRepository articleRepository;
    ArticleService articleService;
    // category
    CategoryRepository categoryRepository;
    CategoryService categoryService;
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

    public NewsApp(HttpRequest request) throws IOException {
        this.request = request;
        DBPool dbPool = new DBPool();
        // afisha
        afishaRepository = new AfishaRepository(dbPool);
        afishaService = new AfishaService(afishaRepository);
        afishaController = new AfishaController(afishaService);
    }
}
