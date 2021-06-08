package news.web.controllers;

import news.dao.specifications.FindAllSourceSpecification;
import news.dao.specifications.FindByIdSourceSpecification;
import news.dao.specifications.FindByTitleSourceSpecification;
import news.model.Source;
import news.service.SourceService;
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
@RequestMapping(value = "/source")
public class SourceController {
    SourceService sourceService;

    public SourceController() {
    }

    @Autowired
    public SourceController(SourceService sourceService) {
        this.sourceService = sourceService;
    }

    @GetMapping(value = "")
    public List<Source> findAllSources(HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindAllSourceSpecification findAll = new FindAllSourceSpecification();
        return sourceService.query(findAll);
    }

    @GetMapping(value = "", params = {"title"})
    public List<Source> findSourcesByTitle(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindByTitleSourceSpecification findByTitle = new FindByTitleSourceSpecification(request.getParameter("title"));
        return sourceService.query(findByTitle);
    }

    @GetMapping(value = "/{id}")
    public Source findSourceById(@PathVariable int id, HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindByIdSourceSpecification findById = new FindByIdSourceSpecification(id);
        List<Source> findByIdSourceList = sourceService.query(findById);
        if (findByIdSourceList.isEmpty()) {
            throw new InstanceNotFoundException();
        }
        return findByIdSourceList.get(0);
    }

    @PostMapping(value = "")
    public void createSource(@RequestBody Source source, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            sourceService.create(source);
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @PutMapping(value = "/{id}")
    public void updateSource(@RequestBody Source source, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            sourceService.update(source);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @DeleteMapping(value = "/{id}")
    public void deleteSource(@PathVariable int id, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            sourceService.delete(id);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }
}
