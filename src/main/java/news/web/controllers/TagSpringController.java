package news.web.controllers;

import news.dao.specifications.FindByIdTagSpecification;
import news.dto.TagSerializer;
import news.model.Tag;
import news.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Тег не обнаружен")  // 404
    public static class TagNotFoundException extends RuntimeException {
        int notFoundId;
        public TagNotFoundException(int id) {
            this.notFoundId = id;
        }
    }

    @GetMapping(value = "")
    public String findAllTags() {

        System.out.println("HELLO WORLD!");
        return "[\n" +
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"name\":\"Димсон\"\n" +
                "},\n" +
                "{\n" +
                "\t\"id\":2,\n" +
                "\t\"name\":\"Артемсон\"\n" +
                "}\n" +
                "]";
    }

    @GetMapping(value = "", params = {"title"})
    public String fingTagByTitle() {
        return "0";
    }

    @GetMapping(value = "/{id}")
    public void findTagById(@PathVariable int id, HttpServletResponse response) throws SQLException, IOException {
        response.setCharacterEncoding("UTF-8");
        FindByIdTagSpecification findById = new FindByIdTagSpecification(id);
        System.out.println("id: " + id);
        List<Tag> findByIdTagList = tagService.query(findById);
        if (findByIdTagList.isEmpty()) {
            throw new TagNotFoundException(id);
        } else {
            Tag tag = findByIdTagList.get(0);
            System.out.println("tag: " + tag);
            tagSerializer = new TagSerializer(tag);
            System.out.println("tagSerializer: " + tagSerializer);
            PrintWriter pr = response.getWriter();
            pr.write(tagSerializer.toJSON());
        }
    }
}
