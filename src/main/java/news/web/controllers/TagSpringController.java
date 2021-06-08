package news.web.controllers;

import news.dao.specifications.FindAllTagSpecification;
import news.dao.specifications.FindByIdTagSpecification;
import news.dao.specifications.FindByTitleTagSpecification;
import news.dto.TagSerializer;
import news.model.Tag;
import news.service.TagService;
import news.web.controllers.exceptions.InstanceNotFoundException;
import news.web.controllers.exceptions.ServerErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(value = "/tag")
public class TagSpringController {
    TagService tagService;
    TagSerializer tagSerializer;

    public TagSpringController() {}

    @Autowired
    public TagSpringController(TagService tagService) {
        this.tagService = tagService;
    }

//    @GetMapping(value = "")
//    public void findAllTags(HttpServletResponse response) throws SQLException {
//        response.setCharacterEncoding("UTF-8");
//        response.setHeader("Content-Type", "application/json");
//        FindAllTagSpecification findAll = new FindAllTagSpecification();
//        List<Tag> findAllTagList = tagService.query(findAll);
//        if (findAllTagList.isEmpty()) {
//            throw new InstanceNotFoundException();
//        } else {
//            try {
//                StringBuilder body = new StringBuilder();
//                for (int i = 0; i < findAllTagList.size(); i++) {
//                    tagSerializer = new TagSerializer(findAllTagList.get(i));
//                    body.append(tagSerializer.toJSON());
//                    if (i != findAllTagList.size() - 1) {
//                        body.append(",\n");
//                    } else {
//                        body.append("\n");
//                    }
//                }
//                body.insert(0, "[\n").append("]\n");
//                PrintWriter pr = response.getWriter();
//                pr.write(body.toString());
//            } catch (Exception e) {
//                throw new ServerErrorException();
//            }
//        }
//    }
    @GetMapping(value = "")
    public List<Tag> findAllTags(HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindAllTagSpecification findAll = new FindAllTagSpecification();
        return tagService.query(findAll);
    }

    @GetMapping(value = "", params = {"title"})
    public void findTagByTitle(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindByTitleTagSpecification findByTitle = new FindByTitleTagSpecification(request.getParameter("title"));
        List<Tag> findByTitleTagList = tagService.query(findByTitle);
        if (findByTitleTagList.isEmpty()) {
            throw new InstanceNotFoundException();
        } else {
            try {
                StringBuilder body = new StringBuilder();
                for (int i = 0; i < findByTitleTagList.size(); i++) {
                    tagSerializer = new TagSerializer(findByTitleTagList.get(i));
                    body.append(tagSerializer.toJSON());
                    if (i != findByTitleTagList.size() - 1) {
                        body.append(",\n");
                    } else {
                        body.append("\n");
                    }
                }
                body.insert(0, "[\n").append("]\n");
                PrintWriter pr = response.getWriter();
                pr.write(body.toString());
            } catch (Exception e) {
                throw new ServerErrorException();
            }
        }
    }

    @GetMapping(value = "/{id}")
    public void findTagById(@PathVariable int id, HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindByIdTagSpecification findById = new FindByIdTagSpecification(id);
        List<Tag> findByIdTagList = tagService.query(findById);
        if (findByIdTagList.isEmpty()) {
            throw new InstanceNotFoundException();
        } else {
            try {
                Tag tag = findByIdTagList.get(0);
                tagSerializer = new TagSerializer(tag);
                PrintWriter pr = response.getWriter();
                pr.write(tagSerializer.toJSON());
            } catch (Exception e) {
                throw new ServerErrorException();
            }
        }
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
