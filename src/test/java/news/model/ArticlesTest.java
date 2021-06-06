//package news.model;
//
//import org.assertj.core.api.SoftAssertions;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
///**
// * Тестирование новостей (Article)
// */
//class ArticlesTest {
//    private static LocalDate editDate;
//    private static LocalDate createDate;
//
//    @Mock
//    final private Article.ArticleImage articleImage = new Article.ArticleImage(1, "Забастовка на площади",
//            "/static/images/zabastovka.jpg", 1);
//
//    @Mock
//    final private Article.ArticleImage articleImage2 = new Article.ArticleImage(2, "Забастовка на площади 2",
//            "/static/images/zabastovka2.jpg", 1);
//
//    @BeforeAll
//    static void beforeAll() {
//        editDate = LocalDate.of(2020, 5, 20);
//        createDate = LocalDate.of(2020, 5, 20);
//    }
//
//    /**
//     * Редактирования новости
//     */
//    @Test
//    void editArticle() {
//        Article article = new Article(1, "title 1", "lead 1", createDate, editDate,
//                "description article 1", true, 1, 1, 1);
//        SoftAssertions soft = new SoftAssertions();
//
//        article.edit("title 2", "lead 2", editDate,
//                "description article 2", false, 2);
//
//        soft.assertThat(article)
//                .hasFieldOrPropertyWithValue("title", "title 2")
//                .hasFieldOrPropertyWithValue("lead", "lead 2")
//                .hasFieldOrPropertyWithValue("editDate", editDate)
//                .hasFieldOrPropertyWithValue("text", "description article 2")
//                .hasFieldOrPropertyWithValue("isPublished", false)
//                .hasFieldOrPropertyWithValue("sourceId", 2);
//        soft.assertAll();
//    }
//
//    /**
//     * Добавить картинку к новости
//     */
//    @Test
//    void addNewImage() {
//        Article article = new Article(1, "title 1", "lead 1", createDate, editDate,
//                "description article 1", true, 1, 1, 1);
//
//        article.addNewImage(articleImage);
//
//        assertThat(article.containImage(articleImage)).as("Объект изображения ArticleImage не был добавлен " +
//                "в список изображений объекта Article").isTrue();
//    }
//
//    /**
//     * Замена списка изображений к новости
//     */
//    @Test
//    void setListImages() {
//        Article article = new Article(1, "title 1", "lead 1", createDate, editDate,
//                "description article 1", true, 1, 1, 1);
//        ArrayList<Article.ArticleImage> articleImages = new ArrayList<>();
//        articleImages.add(articleImage);
//        articleImages.add(articleImage2);
//
//        article.setAllImages(articleImages);
//
//        assertThat(article.containImage(articleImage) && article.containImage(articleImage2)).as("Список " +
//                "объектов изображения ArticleImage не был добавлен в список изображений объекта Article").isTrue();
//    }
//
//    /**
//     * Добавление id тега
//     */
//    @Test
//    void addNewTagId() {
//        Article article = new Article(1, "title 1", "lead 1", createDate, editDate,
//                "description article 1", true, 1, 1, 1);
//        Integer tagId = 1;
//
//        article.addNewTagId(tagId);
//
//        assertThat(article.containTag(tagId)).as("ID тега не был добавлен в список id тегов объекта Article").isTrue();
//    }
//
//    /**
//     * Замена списка тегов к новости
//     */
//    @Test
//    void setListTagsId() {
//        Article article = new Article(1, "title 1", "lead 1", createDate, editDate,
//                "description article 1", true, 1, 1, 1);
//        ArrayList<Integer> articleTagsId = new ArrayList<>();
//        articleTagsId.add(1);
//        articleTagsId.add(2);
//        articleTagsId.add(3);
//
//        article.setAllTagsId(articleTagsId);
//
//        assertThat(article.containTag(1) && article.containTag(2) && article.containTag(3)).as("Список id " +
//                "тегов не был добавлены в список тегов объекта Article").isTrue();
//    }
//
//    /**
//     * Снять новость с публикации
//     * */
//    @Test
//    void unpublished() {
//        Article article = new Article(1, "title 1", "lead 1", createDate, editDate,
//                "description article 1", true, 1, 1, 1);
//
//        article.unpublished();
//
//        assertThat(article.getStatusPublished()).as("Страница не снята с публикации").isFalse();
//    }
//
//    /**
//     * Опубликовать новость
//     * */
//    @Test
//    void published() {
//        Article article = new Article(1, "title 1", "lead 1", createDate, editDate,
//                "description article 1", true, 1, 1, 1);
//
//        article.published();
//
//        assertThat(article.getStatusPublished()).as("Страница не опубликована").isTrue();
//    }
//}