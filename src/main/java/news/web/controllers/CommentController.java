package news.web.controllers;

import news.model.Comment;
import news.service.CommentService;
import news.web.controllers.exceptions.InstanceNotFoundException;
import news.web.controllers.exceptions.ServerErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

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
    public List<Comment> findAllComments(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        return commentService.findAll();
    }

    @GetMapping(value = "", params = {"userid"})
    public List<Comment> findCommentsByTitle(HttpServletRequest request, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        return commentService.findByUserId(Integer.parseInt(request.getParameter("userid")));
    }

    @GetMapping(value = "/{id}")
    public Optional<Comment> findCommentById(@PathVariable int id, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        Optional<Comment> comment = commentService.findById(id);
        if (comment.isEmpty()) {
            throw new InstanceNotFoundException();
        }
        return comment;
    }

    @PostMapping(value = "")
    public void createComment(@RequestBody Comment comment, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            commentService.createComment(comment);
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @PutMapping(value = "/{id}")
    public void updateComment(@RequestBody Comment comment, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            commentService.updateComment(comment);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @DeleteMapping(value = "/{id}")
    public void deleteComment(@PathVariable int id, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            commentService.deleteComment(id);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }
}
