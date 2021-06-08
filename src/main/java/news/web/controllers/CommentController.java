package news.web.controllers;

import news.dao.specifications.FindAllCommentSpecification;
import news.dao.specifications.FindByIdCommentSpecification;
import news.dao.specifications.FindByUserIdCommentSpecification;
import news.model.Comment;
import news.service.CommentService;
import news.web.controllers.exceptions.InstanceNotFoundException;
import news.web.controllers.exceptions.ServerErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(value = "/comment")
public class CommentController {
    CommentService commentService;

    public CommentController() {
    }

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping(value = "")
    public List<Comment> findAllComments(HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindAllCommentSpecification findAll = new FindAllCommentSpecification();
        return commentService.query(findAll);
    }

    @GetMapping(value = "", params = {"userid"})
    public List<Comment> findCommentsByTitle(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindByUserIdCommentSpecification findByTitle = new FindByUserIdCommentSpecification(Integer.parseInt(request.getParameter("userid")));
        return commentService.query(findByTitle);
    }

    @GetMapping(value = "/{id}")
    public Comment findCommentById(@PathVariable int id, HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindByIdCommentSpecification findById = new FindByIdCommentSpecification(id);
        List<Comment> findByIdCommentList = commentService.query(findById);
        if (findByIdCommentList.isEmpty()) {
            throw new InstanceNotFoundException();
        }
        return findByIdCommentList.get(0);
    }

    @PostMapping(value = "")
    public void createComment(@RequestBody Comment comment, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            commentService.create(comment);
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @PutMapping(value = "/{id}")
    public void updateComment(@RequestBody Comment comment, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            commentService.update(comment);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @DeleteMapping(value = "/{id}")
    public void deleteComment(@PathVariable int id, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            commentService.delete(id);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }
}
