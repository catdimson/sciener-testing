package news.service;

import news.dao.repositories.GroupRepository;
import news.model.Group;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@DisplayName("Тестирование сервиса для Group")
@SpringBootTest
@RunWith(SpringRunner.class)
class GroupServiceImplTest {

    @MockBean
    private GroupRepository groupRepository;

    @Mock
    private Group group;

    @Autowired
    private GroupServiceImpl groupService;

    @DisplayName("Получение всех записей")
    @Test
    void findAll() {
        groupService.findAll();

        Mockito.verify(groupRepository, Mockito.times(1)).findAll();
    }

    @DisplayName("Поиск по заголовку")
    @Test
    void findByTitle() {
        groupService.findByTitle("Some_title");

        Mockito.verify(groupRepository, Mockito.times(1)).findByTitle("Some_title");
    }

    @DisplayName("Получение по ID")
    @Test
    void findById() {
        groupService.findById(1);

        Mockito.verify(groupRepository, Mockito.times(1)).findById(1);
    }

    @DisplayName("Сохранение сущности")
    @Test
    void createGroup() {
        groupService.createGroup(group);

        Mockito.verify(groupRepository, Mockito.times(1)).save(group);
    }

    @DisplayName("Обновление сущности")
    @Test
    void updateGroup() {
        groupService.updateGroup(group);

        Mockito.verify(groupRepository, Mockito.times(1)).save(group);
    }

    @DisplayName("Удаление сущности")
    @Test
    void deleteGroup() {
        groupService.deleteGroup(1);

        Mockito.verify(groupRepository, Mockito.times(1)).deleteById(1);
    }
}