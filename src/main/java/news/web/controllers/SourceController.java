package news.web.controllers;

import news.model.Source;
import news.service.SourceService;
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
@RequestMapping(value = "/source")
public class SourceController {
    SourceService sourceService;

    public SourceController() {
    }

    @Autowired
    public SourceController(SourceService sourceService) {
        this.sourceService = sourceService;
    }

    @GetMapping(value = "/")
    public List<Source> findAllSources(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        return sourceService.findAll();
    }

    @GetMapping(value = "/", params = {"title"})
    public List<Source> findSourcesByTitle(HttpServletRequest request, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        return sourceService.findByTitle(request.getParameter("title"));
    }

    @GetMapping(value = "/{id}/")
    public Optional<Source> findSourceById(@PathVariable int id, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        Optional<Source> source = sourceService.findById(id);
        if (source.isEmpty()) {
            throw new InstanceNotFoundException();
        }
        return source;
    }

    @PostMapping(value = "/")
    public void createSource(@RequestBody Source source, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            sourceService.createSource(source);
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @PutMapping(value = "/{id}/")
    public void updateSource(@RequestBody Source source, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            sourceService.updateSource(source);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @DeleteMapping(value = "/{id}/")
    public void deleteSource(@PathVariable int id, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            sourceService.deleteSource(id);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }
}
