package news.dto;

import news.model.Article;
import news.model.ArticleImage;
import news.model.Tag;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
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
        Article article = new Article(1,"Заголовок 1", "Лид 1", Timestamp.valueOf(createDateArticle.atStartOfDay()),
                Timestamp.valueOf(editDateArticle.atStartOfDay()),"Текст 1", true, 1, 1, 1);
        ArticleImage articleImage1 = new ArticleImage(1, "Изображение 1", "/static/images/image1.png");
        ArticleImage articleImage2 = new ArticleImage(2, "Изображение 2", "/static/images/image2.png");
        article.addNewImage(articleImage1);
        article.addNewImage(articleImage2);
        articleImage1.setArticle(article);
        articleImage2.setArticle(article);
        Tag tag1 = new Tag(1, "new tag 1");
        Tag tag2 = new Tag(2, "new tag 2");
        article.addNewTag(tag1);
        article.addNewTag(tag2);
        tag1.addNewArticle(article);
        tag2.addNewArticle(article);
        final String expectedJSON =
                "{\n" +
                "\t\"id\": 1,\n" +
                "\t\"title\": \"Заголовок 1\",\n" +
                "\t\"lead\": \"Лид 1\",\n" +
                "\t\"createDate\": " + Timestamp.valueOf(createDateArticle.atStartOfDay()).getTime() / 1000 + ",\n" +
                "\t\"editDate\": " + Timestamp.valueOf(editDateArticle.atStartOfDay()).getTime() / 1000 + ",\n" +
                "\t\"text\": \"Текст 1\",\n" +
                "\t\"isPublished\": true,\n" +
                "\t\"categoryId\": 1,\n" +
                "\t\"userId\": 1,\n" +
                "\t\"sourceId\": 1,\n" +
                "\t\"images\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"id\": 1,\n" +
                "\t\t\t\"title\": \"Изображение 1\",\n" +
                "\t\t\t\"path\": \"/static/images/image1.png\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\": 2,\n" +
                "\t\t\t\"title\": \"Изображение 2\",\n" +
                "\t\t\t\"path\": \"/static/images/image2.png\"\n" +
                "\t\t}\n" +
                "\t],\n" +
                "\t\"tagsId\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"id\": 1,\n" +
                "\t\t\t\"title\": \"new tag 1\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\": 2,\n" +
                "\t\t\t\"title\": \"new tag 2\"\n" +
                "\t\t}\n" +
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
                "\t\"id\": 1,\n" +
                "\t\"title\": \"Заголовок 1\",\n" +
                "\t\"lead\": \"Лид 1\",\n" +
                "\t\"createDate\": " + Timestamp.valueOf(createDateArticle.atStartOfDay()).getTime() + ",\n" +
                "\t\"editDate\": " + Timestamp.valueOf(editDateArticle.atStartOfDay()).getTime() + ",\n" +
                "\t\"text\": \"Текст 1\",\n" +
                "\t\"isPublished\": true,\n" +
                "\t\"categoryId\": 1,\n" +
                "\t\"userId\": 1,\n" +
                "\t\"sourceId\": 1,\n" +
                "\t\"images\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"id\": 1,\n" +
                "\t\t\t\"title\": \"Изображение 1\",\n" +
                "\t\t\t\"path\": \"/static/images/image1.png\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\": 2,\n" +
                "\t\t\t\"title\": \"Изображение 2\",\n" +
                "\t\t\t\"path\": \"/static/images/image2.png\"\n" +
                "\t\t},\n" +
                "\t],\n" +
                "\t\"tagsId\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"id\": 1,\n" +
                "\t\t\t\"title\": \"new tag 1\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\": 2,\n" +
                "\t\t\t\"title\": \"new tag 2\"\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}";

        ArticleSerializer articleSerializer = new ArticleSerializer(json);
        Article article = articleSerializer.toObject();

        // сверяем данные
        Object[] articleInstance = article.getObjects();
        List listImageObjects = (ArrayList) articleInstance[10];
        List listTags = (ArrayList) articleInstance[11];
        Tag tag1 = (Tag) listTags.get(0);
        Tag tag2 = (Tag) listTags.get(1);
        ArticleImage articleImage1 = (ArticleImage) listImageObjects.get(0);
        ArticleImage articleImage2 = (ArticleImage) listImageObjects.get(1);
        soft.assertThat(article)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("title", "Заголовок 1")
                .hasFieldOrPropertyWithValue("lead", "Лид 1")
                .hasFieldOrPropertyWithValue("createDate", Timestamp.valueOf(createDateArticle.atStartOfDay()))
                .hasFieldOrPropertyWithValue("editDate", Timestamp.valueOf(editDateArticle.atStartOfDay()))
                .hasFieldOrPropertyWithValue("text", "Текст 1")
                .hasFieldOrPropertyWithValue("isPublished", true)
                .hasFieldOrPropertyWithValue("categoryId", 1)
                .hasFieldOrPropertyWithValue("userId", 1)
                .hasFieldOrPropertyWithValue("sourceId", 1);
        soft.assertAll();
        soft.assertThat(articleImage1)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("title", "Изображение 1")
                .hasFieldOrPropertyWithValue("path", "/static/images/image1.png");
        soft.assertAll();
        soft.assertThat(articleImage2)
                .hasFieldOrPropertyWithValue("id", 2)
                .hasFieldOrPropertyWithValue("title", "Изображение 2")
                .hasFieldOrPropertyWithValue("path", "/static/images/image2.png");
        soft.assertAll();
        soft.assertThat(tag1)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("title", "new tag 1");
        soft.assertAll();
        soft.assertThat(tag2)
                .hasFieldOrPropertyWithValue("id", 2)
                .hasFieldOrPropertyWithValue("title", "new tag 2");
        soft.assertAll();
    }
}