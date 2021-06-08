package news.web.controllers;

import news.model.Tag;
import news.service.TagService;
import news.web.controllers.exceptions.InstanceNotFoundException;
import news.web.controllers.exceptions.ServerErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/tag")
public class TagController {
    TagService tagService;

    public TagController() {
    }

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping(value = "")
    public List<Tag> findAllTags(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        return tagService.findAll();
    }

    @GetMapping(value = "", params = {"title"})
    public List<Tag> findTagsByTitle(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        return tagService.findByTitle(request.getParameter("title"));
    }

    @GetMapping(value = "/{id}")
    public Optional<Tag> findTagById(@PathVariable int id, HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        Optional<Tag> tag = tagService.findById(id);
        if (tag.isEmpty()) {
            throw new InstanceNotFoundException();
        }
        return tag;
    }

    @PostMapping(value = "")
    public void createTag(@RequestBody Tag tag, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            tagService.createTag(tag);
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @PutMapping(value = "/{id}")
    public void updateTag(@RequestBody Tag tag, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            tagService.updateTag(tag);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @DeleteMapping(value = "/{id}")
    public void deleteTag(@PathVariable int id, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            tagService.deleteTag(id);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }
}
