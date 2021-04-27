package news.service;

import news.dao.repositories.CommentRepository;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Comment;

import java.sql.SQLException;
import java.util.List;

public class CommentService implements Service<Comment> {
    final private CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<Comment> query(ExtendSqlSpecification<Comment> specification) throws SQLException {
        return commentRepository.query(specification);
    }

    @Override
    public void create(Comment instance) throws SQLException {
        commentRepository.create(instance);
    }

    @Override
    public void delete(int id) throws SQLException {
        commentRepository.delete(id);
    }

    @Override
    public void update(Comment instance) throws SQLException {
        commentRepository.update(instance);
    }
}
