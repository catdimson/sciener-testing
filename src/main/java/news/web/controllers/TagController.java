package news.web.controllers;

import news.dao.specifications.FindAllTagSpecification;
import news.dao.specifications.FindByIdTagSpecification;
import news.dao.specifications.FindByTitleTagSpecification;
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

@RestController
@RequestMapping(value = "/tag")
public class TagController {
    TagService tagService;

    public TagController() {}

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping(value = "")
    public List<Tag> findAllTags(HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindAllTagSpecification findAll = new FindAllTagSpecification();
        return tagService.query(findAll);
    }

    @GetMapping(value = "", params = {"title"})
    public List<Tag> findTagsByTitle(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindByTitleTagSpecification findByTitle = new FindByTitleTagSpecification(request.getParameter("title"));
        return tagService.query(findByTitle);
    }

    @GetMapping(value = "/{id}")
    public Tag findTagById(@PathVariable int id, HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindByIdTagSpecification findById = new FindByIdTagSpecification(id);
        List<Tag> findByIdTagList = tagService.query(findById);
        if (findByIdTagList.isEmpty()) {
            throw new InstanceNotFoundException();
        }
        return findByIdTagList.get(0);
    }

    @PostMapping(value = "")
    public void createTag(@RequestBody Tag tag, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            tagService.create(tag);
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @PutMapping(value = "/{id}")
    public void updateTag(@RequestBody Tag tag, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            tagService.update(tag);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @DeleteMapping(value = "/{id}")
    public void deleteTag(@PathVariable int id, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            tagService.delete(id);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }
}
