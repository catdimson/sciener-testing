package news.dto;

import news.model.Article;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleSerializerTest {
    private static LocalDate createDateArticle;
    private static LocalDate editDateArticle;

    @BeforeAll
    static void beforeAll() {
        createDateArticle = LocalDate.of(2019, 4, 25);
        editDateArticle = LocalDate.of(2019, 6, 25);
    }

    @Test
    void toJSON() {
        Article article = new Article(1,"Заголовок 1", "Лид 1", createDateArticle, editDateArticle,
                "Текст 1", true, 1, 1, 1);
        Article.ArticleImage articleImage1 = new Article.ArticleImage(1, "Изображение 1", "/static/images/image1.png", 1);
        Article.ArticleImage articleImage2 = new Article.ArticleImage(2, "Изображение 2", "/static/images/image2.png", 1);
        article.addNewImage(articleImage1);
        article.addNewImage(articleImage2);
        article.addNewTagId(1);
        article.addNewTagId(2);
        article.addNewTagId(3);
        final String expectedJSON =
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"title\":\"Заголовок 1\",\n" +
                "\t\"lead\":\"Лид 1\",\n" +
                "\t\"createDate\":" + Timestamp.valueOf(createDateArticle.atStartOfDay()).getTime() / 1000 + ",\n" +
                "\t\"editDate\":" + Timestamp.valueOf(editDateArticle.atStartOfDay()).getTime() / 1000 + ",\n" +
                "\t\"text\":\"Текст 1\",\n" +
                "\t\"isPublished\":true,\n" +
                "\t\"categoryId\":1,\n" +
                "\t\"userId\":1,\n" +
                "\t\"sourceId\":1,\n" +
                "\t\"images\":[\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":1,\n" +
                "\t\t\t\"title\":\"Изображение 1\",\n" +
                "\t\t\t\"path\":\"/static/images/image1.png\",\n" +
                "\t\t\t\"articleId\":1,\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":2,\n" +
                "\t\t\t\"title\":\"Изображение 2\",\n" +
                "\t\t\t\"path\":\"/static/images/image2.png\",\n" +
                "\t\t\t\"articleId\":1,\n" +
                "\t\t},\n" +
                "\t],\n" +
                "\t\"tagsId\":[\n" +
                "\t\t1,\n" +
                "\t\t2,\n" +
                "\t\t3,\n" +
                "\t]\n" +
                "}";

        ArticleSerializer articleSerializer = new ArticleSerializer(article);
        String result = articleSerializer.toJSON();

        assertThat(result).isEqualTo(expectedJSON);
    }

    @Test
    void toObject() {
        SoftAssertions soft = new SoftAssertions();
        final String json =
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"title\":\"Заголовок 1\",\n" +
                "\t\"lead\":\"Лид 1\",\n" +
                "\t\"createDate\":" + Timestamp.valueOf(createDateArticle.atStartOfDay()).getTime() / 1000 + ",\n" +
                "\t\"editDate\":" + Timestamp.valueOf(editDateArticle.atStartOfDay()).getTime() / 1000 + ",\n" +
                "\t\"text\":\"Текст 1\",\n" +
                "\t\"isPublished\":true,\n" +
                "\t\"categoryId\":1,\n" +
                "\t\"userId\":1,\n" +
                "\t\"sourceId\":1,\n" +
                "\t\"images\":[\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":1,\n" +
                "\t\t\t\"title\":\"Изображение 1\",\n" +
                "\t\t\t\"path\":\"/static/images/image1.png\",\n" +
                "\t\t\t\"articleId\":1,\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":2,\n" +
                "\t\t\t\"title\":\"Изображение 2\",\n" +
                "\t\t\t\"path\":\"/static/images/image2.png\",\n" +
                "\t\t\t\"articleId\":1,\n" +
                "\t\t},\n" +
                "\t],\n" +
                "\t\"tagsId\":[\n" +
                "\t\t1,\n" +
                "\t\t2,\n" +
                "\t\t3,\n" +
                "\t]\n" +
                "}";

        ArticleSerializer articleSerializer = new ArticleSerializer(json);
        Article article = articleSerializer.toObject();

        // сверяем данные
        Object[] articleInstance = article.getObjects();
        List listImageObjects = (ArrayList) articleInstance[10];
        List tagsId = new ArrayList((HashSet) articleInstance[11]);
        int tagId1 = (int) tagsId.get(0);
        int tagId2 = (int) tagsId.get(1);
        int tagId3 = (int) tagsId.get(2);
        Article.ArticleImage articleImage1 = (Article.ArticleImage) listImageObjects.get(0);
        Article.ArticleImage articleImage2 = (Article.ArticleImage) listImageObjects.get(1);
        soft.assertThat(article)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("title", "Заголовок 1")
                .hasFieldOrPropertyWithValue("lead", "Лид 1")
                .hasFieldOrPropertyWithValue("createDate", createDateArticle)
                .hasFieldOrPropertyWithValue("editDate", editDateArticle)
                .hasFieldOrPropertyWithValue("text", "Текст 1")
                .hasFieldOrPropertyWithValue("isPublished", true)
                .hasFieldOrPropertyWithValue("categoryId", 1)
                .hasFieldOrPropertyWithValue("userId", 1)
                .hasFieldOrPropertyWithValue("sourceId", 1);
        soft.assertAll();
        soft.assertThat(articleImage1)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("title", "Изображение 1")
                .hasFieldOrPropertyWithValue("path", "/static/images/image1.png")
                .hasFieldOrPropertyWithValue("articleId", 1);
        soft.assertAll();
        soft.assertThat(articleImage2)
                .hasFieldOrPropertyWithValue("id", 2)
                .hasFieldOrPropertyWithValue("title", "Изображение 2")
                .hasFieldOrPropertyWithValue("path", "/static/images/image2.png")
                .hasFieldOrPropertyWithValue("articleId", 1);
        soft.assertAll();
        assertThat(tagId1).isEqualTo(1);
        assertThat(tagId2).isEqualTo(2);
        assertThat(tagId3).isEqualTo(3);
    }
}