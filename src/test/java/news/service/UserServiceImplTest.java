package news.service;

import news.dao.repositories.UserRepository;
import news.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@DisplayName("Тестирование сервиса для User")
@SpringBootTest
@RunWith(SpringRunner.class)
class UserServiceImplTest {

    @MockBean
    private UserRepository userRepository;

    @Mock
    private User user;

    @Autowired
    private UserServiceImpl userService;

    @DisplayName("Получение всех записей")
    @Test
    void findAll() {
        userService.findAll();

        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }

    @DisplayName("Поиск по имени")
    @Test
    void findByTitle() {
        userService.findByFirstName("Firstname");

        Mockito.verify(userRepository, Mockito.times(1)).findByFirstName("Firstname");
    }

    @DisplayName("Получение по ID")
    @Test
    void findById() {
        userService.findById(1);

        Mockito.verify(userRepository, Mockito.times(1)).findById(1);
    }

    @DisplayName("Сохранение сущности")
    @Test
    void createUser() {
        userService.createUser(user);

        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @DisplayName("Обновление сущности")
    @Test
    void updateUser() {
        userService.updateUser(user);

        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @DisplayName("Удаление сущности")
    @Test
    void deleteUser() {
        userService.deleteUser(1);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1);
    }
}