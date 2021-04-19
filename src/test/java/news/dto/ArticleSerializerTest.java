package news.dto;

import news.model.Article;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class ArticleSerializerTest {
    private static LocalDate createDateArticle;
    private static LocalDate editDateArticle;

    @BeforeAll
    static void beforeAll() {
        // article (дата создания, дата редактирования, id юзера создавший новость)
        createDateArticle = LocalDate.of(2019, 4, 25);
        editDateArticle = LocalDate.of(2019, 6, 25);
    }

    @Test
    void toJSON() throws ClassNotFoundException {
        Article article = new Article(1,"Заголовок 1", "Лид 1", createDateArticle, editDateArticle,
                "Текст 1", true, 1, 1, 1);
        Article.ArticleImage articleImage1 = new Article.ArticleImage(1, "Изображение 1", "/static/images/image1.png", 1);
        Article.ArticleImage articleImage2 = new Article.ArticleImage(2, "Изображение 2", "/static/images/image2.png", 1);
        article.addNewImage(articleImage1);
        article.addNewImage(articleImage2);
        article.addNewTagId(1);
        article.addNewTagId(2);
        article.addNewTagId(3);

        ArticleSerializer articleSerializer = new ArticleSerializer(article);
        String result = articleSerializer.toJSON();

        System.out.println(result);
    }

    @Test
    void toObject() {

    }
}