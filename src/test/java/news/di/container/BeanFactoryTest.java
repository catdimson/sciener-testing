package news.di.container;

import news.dao.connection.DBPool;
import news.web.controllers.AfishaController;
import news.web.controllers.ArticleController;
import news.web.controllers.CategoryController;
import news.web.controllers.UserController;
import news.web.http.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тестрование создания DI контейнера")
class BeanFactoryTest {

    HttpRequest httpRequest = Mockito.mock(HttpRequest.class);
    DBPool connectionPool = Mockito.mock(DBPool.class);

    @DisplayName("Создание DI из несуществующего файла")
    @Test
    void createDIFromNullXML() throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        BeanFactory.setSettings(connectionPool, httpRequest, "error/path/to/file.xml");
        BeanFactory beanFactory = BeanFactory.getInstance();

        UserController userController = beanFactory.getBean(UserController.class);

        assertThat(userController).as("Указанного файла не существует. userController должен быть null").isNull();
    }

    @DisplayName("Создание DI из пустого файла")
    @Test
    void createDIFromEmptyXML() throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        BeanFactory.setSettings(connectionPool, httpRequest, "src/test/resources/di/emptyXML.xml");
        BeanFactory beanFactory = BeanFactory.getInstance();

        UserController userController = beanFactory.getBean(UserController.class);

        assertThat(userController).as("Файл пуст. userController должен быть null").isNull();
    }

    @DisplayName("Создание DI из корректного файла")
    @Test
    void createDIFromCorrectXML() throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        BeanFactory.setSettings(connectionPool, httpRequest, "src/test/resources/di/correctXML.xml");
        BeanFactory beanFactory = BeanFactory.getInstance();

        CategoryController categoryController = beanFactory.getBean(CategoryController.class);
        ArticleController articleController = beanFactory.getBean(ArticleController.class);
        AfishaController afishaController = beanFactory.getBean(AfishaController.class);

        assertThat(categoryController).as("Объект должен быть класса CategoryController").isInstanceOf(CategoryController.class);
        assertThat(articleController).as("Объект должен быть класса ArticleController").isInstanceOf(ArticleController.class);
        assertThat(afishaController).as("Объект должен быть класса AfishaController").isInstanceOf(AfishaController.class);
    }

    @DisplayName("Создание DI из файла с ошибкой")
    @Test
    void createDIFromErrorXML() throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        BeanFactory.setSettings(connectionPool, httpRequest, "src/test/resources/di/errorXML.xml");
        BeanFactory beanFactory = BeanFactory.getInstance();

        ArticleController articleController = beanFactory.getBean(ArticleController.class);

        assertThat(articleController).as("Файл с некорректными тегами. Объект класса ArticleController должен быть равен null").isNull();
    }
}