package news.service;

import news.dao.repositories.CommentRepository;
import news.model.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@DisplayName("Тестирование сервиса для Comment")
@SpringBootTest
@RunWith(SpringRunner.class)
class CommentServiceImplTest {

    @MockBean
    private CommentRepository commentRepository;

    @Mock
    private Comment comment;

    @Autowired
    private CommentServiceImpl commentService;

    @DisplayName("Получение всех записей")
    @Test
    void findAll() {
        commentService.findAll();

        Mockito.verify(commentRepository, Mockito.times(1)).findAll();
    }

    @DisplayName("Поиск по ID пользователя")
    @Test
    void findByTitle() {
        commentService.findByUserId(1);

        Mockito.verify(commentRepository, Mockito.times(1)).findByUserId(1);
    }

    @DisplayName("Получение по ID")
    @Test
    void findById() {
        commentService.findById(1);

        Mockito.verify(commentRepository, Mockito.times(1)).findById(1);
    }

    @DisplayName("Сохранение сущности")
    @Test
    void createComment() {
        commentService.createComment(comment);

        Mockito.verify(commentRepository, Mockito.times(1)).save(comment);
    }

    @DisplayName("Обновление сущности")
    @Test
    void updateComment() {
        commentService.updateComment(comment);

        Mockito.verify(commentRepository, Mockito.times(1)).save(comment);
    }

    @DisplayName("Удаление сущности")
    @Test
    void deleteComment() {
        commentService.deleteComment(1);

        Mockito.verify(commentRepository, Mockito.times(1)).deleteById(1);
    }
}