package news.dao.repositories;

import news.model.Article;
import news.model.ArticleImage;
import news.model.Tag;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тестирование репозитория для Article")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = ArticleRepositoryTest.Initializer.class)
class ArticleRepositoryTest {
    private static Timestamp createDateArticle;
    private static Timestamp editDateArticle;

    @BeforeAll
    static void setUp() {
        createDateArticle = new Timestamp(1561410000000L);
        editDateArticle = new Timestamp(1561410000000L);
    }

    @Autowired
    private ArticleRepository articleRepository;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.2")
            .withPassword("testrootroot")
            .withUsername("testroot")
            .withDatabaseName("testnewdb");

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues values = TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername()
            );
            values.applyTo(configurableApplicationContext);
        }
    }

    @DisplayName("Получение по ID")
    @Test
    @Transactional
    @Sql(scripts = "classpath:repository-scripts/deployment/article.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/article.sql")
    void findById() {
        SoftAssertions soft = new SoftAssertions();
        // статьи
        Article article1 = new Article(1, "Заголовок 1", "Лид 1", createDateArticle,
                editDateArticle, "Текст 1", true, 1, 1, 1);
        // изображения
        ArticleImage articleImage1 = new ArticleImage("Изображение 1", "/static/images/image1.png");
        article1.addNewImage(articleImage1);
        articleImage1.setArticle(article1);
        // тэги
        Tag tag1 = new Tag("Тег 1");
        article1.addNewTag(tag1);
        tag1.addNewArticle(article1);

        // получаем список статей
        Optional<Article> resultArticle = articleRepository.findById(1);
        Article resultArticle1 = resultArticle.get();
        // получаем изображения
        ArticleImage resultArticleImage1 = (ArticleImage) resultArticle1.getImages().toArray()[0];
        // получаем теги
        Tag resultTag1 = (Tag) resultArticle1.getTags().toArray()[0];

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(resultArticle1)
                .hasFieldOrPropertyWithValue("title", article1.getObjects()[1])
                .hasFieldOrPropertyWithValue("lead", article1.getObjects()[2])
                .hasFieldOrPropertyWithValue("createDate", article1.getObjects()[3])
                .hasFieldOrPropertyWithValue("editDate", article1.getObjects()[4])
                .hasFieldOrPropertyWithValue("text", article1.getObjects()[5])
                .hasFieldOrPropertyWithValue("isPublished", article1.getObjects()[6])
                .hasFieldOrPropertyWithValue("userId", article1.getObjects()[7])
                .hasFieldOrPropertyWithValue("sourceId", article1.getObjects()[8]);
        soft.assertAll();
        soft.assertThat(resultArticleImage1)
                .hasFieldOrPropertyWithValue("title", articleImage1.getObjects()[1])
                .hasFieldOrPropertyWithValue("path", articleImage1.getObjects()[2]);
        soft.assertAll();
        assertThat(resultTag1).hasFieldOrPropertyWithValue("title", tag1.getObjects()[1]);
    }

    @DisplayName("Получение всех записей")
    @Test
    @Transactional
    @Sql(scripts = "classpath:repository-scripts/deployment/article.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/article.sql")
    void findAll() {
        SoftAssertions soft = new SoftAssertions();
        // статьи
        Article article1 = new Article("Заголовок 1", "Лид 1", createDateArticle,
                editDateArticle, "Текст 1", true, 1, 1, 1);
        Article article2 = new Article("Заголовок 1", "Лид 2", createDateArticle,
                editDateArticle, "Текст 2", true, 2, 2, 2);
        Article article3 = new Article("Заголовок 3", "Лид 3", createDateArticle,
                editDateArticle, "Текст 3", true, 2, 2, 2);

        // изображения
        ArticleImage articleImage1 = new ArticleImage("Изображение 1", "/static/images/image1.png");
        ArticleImage articleImage2 = new ArticleImage("Изображение 2", "/static/images/image2.png");
        article1.addNewImage(articleImage1);
        article2.addNewImage(articleImage2);
        articleImage1.setArticle(article1);
        articleImage2.setArticle(article2);
        // тэги
        Tag tag1 = new Tag("Тег 1");
        Tag tag2 = new Tag("Тег 2");
        article1.addNewTag(tag1);
        article2.addNewTag(tag2);
        tag1.addNewArticle(article1);
        tag2.addNewArticle(article2);

        // получаем список статей
        List<Article> resultArticle = articleRepository.findAll();
        Article resultArticle1 = resultArticle.get(0);
        Article resultArticle2 = resultArticle.get(1);
        Article resultArticle3 = resultArticle.get(2);
        // получаем изображения
        ArticleImage resultArticleImage1 = (ArticleImage) resultArticle1.getImages().toArray()[0];
        ArticleImage resultArticleImage2 = (ArticleImage) resultArticle2.getImages().toArray()[0];
        // получаем теги
        Tag resultTag1 = (Tag) resultArticle1.getTags().toArray()[0];
        Tag resultTag2 = (Tag) resultArticle2.getTags().toArray()[0];

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(resultArticle1)
                .hasFieldOrPropertyWithValue("title", article1.getObjects()[1])
                .hasFieldOrPropertyWithValue("lead", article1.getObjects()[2])
                .hasFieldOrPropertyWithValue("createDate", article1.getObjects()[3])
                .hasFieldOrPropertyWithValue("editDate", article1.getObjects()[4])
                .hasFieldOrPropertyWithValue("text", article1.getObjects()[5])
                .hasFieldOrPropertyWithValue("isPublished", article1.getObjects()[6])
                .hasFieldOrPropertyWithValue("userId", article1.getObjects()[7])
                .hasFieldOrPropertyWithValue("sourceId", article1.getObjects()[8]);
        soft.assertAll();
        soft.assertThat(resultArticle2)
                .hasFieldOrPropertyWithValue("title", article2.getObjects()[1])
                .hasFieldOrPropertyWithValue("lead", article2.getObjects()[2])
                .hasFieldOrPropertyWithValue("createDate", article2.getObjects()[3])
                .hasFieldOrPropertyWithValue("editDate", article2.getObjects()[4])
                .hasFieldOrPropertyWithValue("text", article2.getObjects()[5])
                .hasFieldOrPropertyWithValue("isPublished", article2.getObjects()[6])
                .hasFieldOrPropertyWithValue("userId", article2.getObjects()[7])
                .hasFieldOrPropertyWithValue("sourceId", article2.getObjects()[8]);
        soft.assertAll();
        soft.assertThat(resultArticle3)
                .hasFieldOrPropertyWithValue("title", article3.getObjects()[1])
                .hasFieldOrPropertyWithValue("lead", article3.getObjects()[2])
                .hasFieldOrPropertyWithValue("createDate", article3.getObjects()[3])
                .hasFieldOrPropertyWithValue("editDate", article3.getObjects()[4])
                .hasFieldOrPropertyWithValue("text", article3.getObjects()[5])
                .hasFieldOrPropertyWithValue("isPublished", article3.getObjects()[6])
                .hasFieldOrPropertyWithValue("userId", article3.getObjects()[7])
                .hasFieldOrPropertyWithValue("sourceId", article3.getObjects()[8]);
        soft.assertAll();
        soft.assertThat(resultArticleImage1)
                .hasFieldOrPropertyWithValue("title", articleImage1.getObjects()[1])
                .hasFieldOrPropertyWithValue("path", articleImage1.getObjects()[2]);
        soft.assertAll();
        soft.assertThat(resultArticleImage2)
                .hasFieldOrPropertyWithValue("title", articleImage2.getObjects()[1])
                .hasFieldOrPropertyWithValue("path", articleImage2.getObjects()[2]);
        soft.assertAll();
        assertThat(resultTag1).hasFieldOrPropertyWithValue("title", tag1.getObjects()[1]);
        assertThat(resultTag2).hasFieldOrPropertyWithValue("title", tag2.getObjects()[1]);
    }

    @DisplayName("Поиск по заголовку")
    @Test
    @Transactional
    @Sql(scripts = "classpath:repository-scripts/deployment/article.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/article.sql")
    void findByTitle() {
        SoftAssertions soft = new SoftAssertions();
        // статьи
        Article article1 = new Article("Заголовок 1", "Лид 1", createDateArticle,
                editDateArticle, "Текст 1", true, 1, 1, 1);
        Article article2 = new Article("Заголовок 1", "Лид 2", createDateArticle,
                editDateArticle, "Текст 2", true, 2, 2, 2);
        // изображения
        ArticleImage articleImage1 = new ArticleImage("Изображение 1", "/static/images/image1.png");
        ArticleImage articleImage2 = new ArticleImage("Изображение 2", "/static/images/image2.png");
        article1.addNewImage(articleImage1);
        article2.addNewImage(articleImage2);
        articleImage1.setArticle(article1);
        articleImage2.setArticle(article2);
        // тэги
        Tag tag1 = new Tag("Тег 1");
        Tag tag2 = new Tag("Тег 2");
        article1.addNewTag(tag1);
        article2.addNewTag(tag2);
        tag1.addNewArticle(article1);
        tag2.addNewArticle(article2);

        // получаем список статей
        List<Article> resultArticle = articleRepository.findByTitle("Заголовок 1");
        Article resultArticle1 = resultArticle.get(0);
        Article resultArticle2 = resultArticle.get(1);
        // получаем изображения
        ArticleImage resultArticleImage1 = (ArticleImage) resultArticle1.getImages().toArray()[0];
        ArticleImage resultArticleImage2 = (ArticleImage) resultArticle2.getImages().toArray()[0];
        // получаем теги
        Tag resultTag1 = (Tag) resultArticle1.getTags().toArray()[0];
        Tag resultTag2 = (Tag) resultArticle2.getTags().toArray()[0];

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(resultArticle1)
                .hasFieldOrPropertyWithValue("title", article1.getObjects()[1])
                .hasFieldOrPropertyWithValue("lead", article1.getObjects()[2])
                .hasFieldOrPropertyWithValue("createDate", article1.getObjects()[3])
                .hasFieldOrPropertyWithValue("editDate", article1.getObjects()[4])
                .hasFieldOrPropertyWithValue("text", article1.getObjects()[5])
                .hasFieldOrPropertyWithValue("isPublished", article1.getObjects()[6])
                .hasFieldOrPropertyWithValue("userId", article1.getObjects()[7])
                .hasFieldOrPropertyWithValue("sourceId", article1.getObjects()[8]);
        soft.assertAll();
        soft.assertThat(resultArticle2)
                .hasFieldOrPropertyWithValue("title", article2.getObjects()[1])
                .hasFieldOrPropertyWithValue("lead", article2.getObjects()[2])
                .hasFieldOrPropertyWithValue("createDate", article2.getObjects()[3])
                .hasFieldOrPropertyWithValue("editDate", article2.getObjects()[4])
                .hasFieldOrPropertyWithValue("text", article2.getObjects()[5])
                .hasFieldOrPropertyWithValue("isPublished", article2.getObjects()[6])
                .hasFieldOrPropertyWithValue("userId", article2.getObjects()[7])
                .hasFieldOrPropertyWithValue("sourceId", article2.getObjects()[8]);
        soft.assertAll();
        soft.assertThat(resultArticleImage1)
                .hasFieldOrPropertyWithValue("title", articleImage1.getObjects()[1])
                .hasFieldOrPropertyWithValue("path", articleImage1.getObjects()[2]);
        soft.assertAll();
        soft.assertThat(resultArticleImage2)
                .hasFieldOrPropertyWithValue("title", articleImage2.getObjects()[1])
                .hasFieldOrPropertyWithValue("path", articleImage2.getObjects()[2]);
        soft.assertAll();
        assertThat(resultTag1).hasFieldOrPropertyWithValue("title", tag1.getObjects()[1]);
        assertThat(resultTag2).hasFieldOrPropertyWithValue("title", tag2.getObjects()[1]);
    }

    @DisplayName("Сохранение сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/article.sql")
    void saveArticle() {
        SoftAssertions soft = new SoftAssertions();
        // статьи
        Article article1 = new Article(1, "Заголовок 1", "Лид 1", createDateArticle,
                editDateArticle, "Текст 1", true, 1, 1, 1);
        // изображения
        ArticleImage articleImage1 = new ArticleImage(1, "Изображение 1", "/static/images/image1.png");
        article1.addNewImage(articleImage1);
        articleImage1.setArticle(article1);
        // тэги
        Tag tag1 = new Tag(1, "Тег 1");
        article1.addNewTag(tag1);
        tag1.addNewArticle(article1);

        Article result = articleRepository.save(article1);
        // получаем изображения
        ArticleImage resultArticleImage1 = (ArticleImage) result.getImages().toArray()[0];
        // получаем теги
        Tag resultTag1 = (Tag) result.getTags().toArray()[0];

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("id", article1.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", article1.getObjects()[1])
                .hasFieldOrPropertyWithValue("lead", article1.getObjects()[2])
                .hasFieldOrPropertyWithValue("createDate", article1.getObjects()[3])
                .hasFieldOrPropertyWithValue("editDate", article1.getObjects()[4])
                .hasFieldOrPropertyWithValue("text", article1.getObjects()[5])
                .hasFieldOrPropertyWithValue("isPublished", article1.getObjects()[6])
                .hasFieldOrPropertyWithValue("userId", article1.getObjects()[7])
                .hasFieldOrPropertyWithValue("sourceId", article1.getObjects()[8]);
        soft.assertAll();
        soft.assertThat(resultArticleImage1)
                .hasFieldOrPropertyWithValue("id", articleImage1.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", articleImage1.getObjects()[1])
                .hasFieldOrPropertyWithValue("path", articleImage1.getObjects()[2]);
        soft.assertAll();
        soft.assertThat(resultTag1)
                .hasFieldOrPropertyWithValue("id", tag1.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", tag1.getObjects()[1]);
        soft.assertAll();
    }

    @DisplayName("Обновление сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/article.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/article.sql")
    void updateArticle() {
        SoftAssertions soft = new SoftAssertions();
        // статьи
        Article article1 = new Article(1, "Заголовок 10", "Лид 10", createDateArticle,
                editDateArticle, "Текст 10", false, 1, 1, 1);
        // изображения
        ArticleImage articleImage1 = new ArticleImage("Изображение 10", "/static/images/image10.png");
        ArticleImage articleImage2 = new ArticleImage(1, "Изображение 11", "/static/images/image11.png");
        article1.addNewImage(articleImage1);
        article1.addNewImage(articleImage2);
        articleImage1.setArticle(article1);
        articleImage2.setArticle(article1);
        // тэги
        Tag tag1 = new Tag("Тег 10");
        Tag tag2 = new Tag(1, "Тег 11");
        article1.addNewTag(tag1);
        article1.addNewTag(tag2);
        tag1.addNewArticle(article1);
        tag2.addNewArticle(article1);

        Article result = articleRepository.save(article1);
        // получаем изображения
        ArticleImage resultArticleImage1 = (ArticleImage) result.getImages().toArray()[0];
        ArticleImage resultArticleImage2 = (ArticleImage) result.getImages().toArray()[1];
        // получаем теги
        Tag resultTag1 = (Tag) result.getTags().toArray()[0];
        Tag resultTag2 = (Tag) result.getTags().toArray()[1];

        // сравниваем полученный результат и ожидаемый
        soft.assertThat(result)
                .hasFieldOrPropertyWithValue("id", article1.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", article1.getObjects()[1])
                .hasFieldOrPropertyWithValue("lead", article1.getObjects()[2])
                .hasFieldOrPropertyWithValue("createDate", article1.getObjects()[3])
                .hasFieldOrPropertyWithValue("editDate", article1.getObjects()[4])
                .hasFieldOrPropertyWithValue("text", article1.getObjects()[5])
                .hasFieldOrPropertyWithValue("isPublished", article1.getObjects()[6])
                .hasFieldOrPropertyWithValue("userId", article1.getObjects()[7])
                .hasFieldOrPropertyWithValue("sourceId", article1.getObjects()[8]);
        soft.assertAll();
        soft.assertThat(resultArticleImage1)
                .hasFieldOrPropertyWithValue("id", 3)
                .hasFieldOrPropertyWithValue("title", articleImage1.getObjects()[1])
                .hasFieldOrPropertyWithValue("path", articleImage1.getObjects()[2]);
        soft.assertAll();
        soft.assertThat(resultArticleImage2)
                .hasFieldOrPropertyWithValue("id", articleImage2.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", articleImage2.getObjects()[1])
                .hasFieldOrPropertyWithValue("path", articleImage2.getObjects()[2]);
        soft.assertAll();
        soft.assertThat(resultTag1)
                .hasFieldOrPropertyWithValue("id", 3)
                .hasFieldOrPropertyWithValue("title", tag1.getObjects()[1]);
        soft.assertAll();
        soft.assertThat(resultTag2)
                .hasFieldOrPropertyWithValue("id", tag2.getObjects()[0])
                .hasFieldOrPropertyWithValue("title", tag2.getObjects()[1]);
        soft.assertAll();
    }

    @DisplayName("Удаление сущности")
    @Test
    @Sql(scripts = "classpath:repository-scripts/deployment/article.sql")
    @Sql(scripts = "classpath:repository-scripts/generate-data/article.sql")
    void deleteArticle() {

        articleRepository.deleteById(1);

        assertThat(articleRepository.existsById(1)).as("Запись типа Article не была удалена").isFalse();
    }
}
