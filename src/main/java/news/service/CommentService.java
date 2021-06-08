package news.service;

import news.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    List<Comment> findAll();

    List<Comment> findByUserId(int userId);

    Optional<Comment> findById(int id);

    Comment createComment(Comment comment);

    Comment updateComment(Comment comment);

    void deleteComment(int id);

}
