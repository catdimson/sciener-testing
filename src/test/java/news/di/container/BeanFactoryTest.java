package news.di.container;

import news.web.controllers.OldAfishaController;
import news.web.controllers.OldArticleController;
import news.web.controllers.OldCategoryController;
import news.web.controllers.OldUserController;
import news.web.http.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тестрование создания DI контейнера")
class BeanFactoryTest {

    HttpRequest httpRequest = Mockito.mock(HttpRequest.class);

    @DisplayName("Создание DI из несуществующего файла")
    @Test
    void createDIFromNullXML() throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        BeanFactory.setSettings(httpRequest, "error/path/to/file.xml");
        BeanFactory beanFactory = BeanFactory.getInstance();

        OldUserController userController = beanFactory.getBean(OldUserController.class);

        assertThat(userController).as("Указанного файла не существует. userController должен быть null").isNull();
    }

    @DisplayName("Создание DI из пустого файла")
    @Test
    void createDIFromEmptyXML() throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        BeanFactory.setSettings(httpRequest, "src/test/resources/di/emptyXML.xml");
        BeanFactory beanFactory = BeanFactory.getInstance();

        OldUserController userController = beanFactory.getBean(OldUserController.class);

        assertThat(userController).as("Файл пуст. userController должен быть null").isNull();
    }

    @DisplayName("Создание DI из корректного файла")
    @Test
    void createDIFromCorrectXML() throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        BeanFactory.setSettings(httpRequest, "src/test/resources/di/correctXML.xml");
        BeanFactory beanFactory = BeanFactory.getInstance();

        OldCategoryController categoryController = beanFactory.getBean(OldCategoryController.class);
        OldArticleController articleController = beanFactory.getBean(OldArticleController.class);
        OldAfishaController afishaController = beanFactory.getBean(OldAfishaController.class);

        assertThat(categoryController).as("Объект должен быть класса CategoryController").isInstanceOf(OldCategoryController.class);
        assertThat(articleController).as("Объект должен быть класса ArticleController").isInstanceOf(OldArticleController.class);
        assertThat(afishaController).as("Объект должен быть класса AfishaController").isInstanceOf(OldAfishaController.class);
    }

    @DisplayName("Создание DI из файла с ошибкой")
    @Test
    void createDIFromErrorXML() throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        BeanFactory.setSettings(httpRequest, "src/test/resources/di/errorXML.xml");
        BeanFactory beanFactory = BeanFactory.getInstance();

        OldArticleController articleController = beanFactory.getBean(OldArticleController.class);

        assertThat(articleController).as("Файл с некорректными тегами. Объект класса ArticleController должен быть равен null").isNull();
    }
}