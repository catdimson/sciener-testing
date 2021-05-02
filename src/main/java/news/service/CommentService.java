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
    public int create(Comment instance) throws SQLException {
        return commentRepository.create(instance);
    }

    @Override
    public int delete(int id) throws SQLException {
        return commentRepository.delete(id);
    }

    @Override
    public int update(Comment instance) throws SQLException {
        return commentRepository.update(instance);
    }
}
