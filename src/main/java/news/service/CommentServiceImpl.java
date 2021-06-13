package news.service;

import news.dao.repositories.CommentRepository;
import news.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class CommentServiceImpl implements CommentService {

    protected CommentRepository commentRepository;

    public CommentServiceImpl() {}

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Override
    public List<Comment> findByUserId(int userId) {
        return commentRepository.findByUserId(userId);
    }

    @Override
    public Optional<Comment> findById(int id) {
        return commentRepository.findById(id);
    }

    @Override
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment updateComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(int id) {
        commentRepository.deleteById(id);
    }
}
