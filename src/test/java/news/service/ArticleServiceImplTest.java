package news.service;

import news.dao.repositories.ArticleRepository;
import news.model.Article;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@DisplayName("Тестирование сервиса для Article")
@SpringBootTest
@RunWith(SpringRunner.class)
class ArticleServiceImplTest {

    @MockBean
    private ArticleRepository articleRepository;

    @Mock
    private Article article;

    @Autowired
    private ArticleServiceImpl articleService;

    @DisplayName("Получение всех записей")
    @Test
    void findAll() {
        articleService.findAll();

        Mockito.verify(articleRepository, Mockito.times(1)).findAll();
    }

    @DisplayName("Поиск по заголовку")
    @Test
    void findByTitle() {
        articleService.findByTitle("Some_title");

        Mockito.verify(articleRepository, Mockito.times(1)).findByTitle("Some_title");
    }

    @DisplayName("Получение по ID")
    @Test
    void findById() {
        articleService.findById(1);

        Mockito.verify(articleRepository, Mockito.times(1)).findById(1);
    }

    @DisplayName("Сохранение сущности")
    @Test
    void createArticle() {
        articleService.createArticle(article);

        Mockito.verify(articleRepository, Mockito.times(1)).save(article);
    }

    @DisplayName("Обновление сущности")
    @Test
    void updateArticle() {
        articleService.updateArticle(article);

        Mockito.verify(articleRepository, Mockito.times(1)).save(article);
    }

    @DisplayName("Удаление сущности")
    @Test
    void deleteArticle() {
        articleService.deleteArticle(1);

        Mockito.verify(articleRepository, Mockito.times(1)).deleteById(1);
    }
}