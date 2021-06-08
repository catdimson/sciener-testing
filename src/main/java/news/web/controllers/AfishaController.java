package news.web.controllers;

import news.dao.specifications.FindAllAfishaSpecification;
import news.dao.specifications.FindByIdAfishaSpecification;
import news.dao.specifications.FindByTitleAfishaSpecification;
import news.model.Afisha;
import news.service.AfishaService;
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
@RequestMapping(value = "/afisha")
public class AfishaController {
    AfishaService afishaService;

    public AfishaController() {
    }

    @Autowired
    public AfishaController(AfishaService afishaService) {
        this.afishaService = afishaService;
    }

    @GetMapping(value = "")
    public List<Afisha> findAllAfishas(HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindAllAfishaSpecification findAll = new FindAllAfishaSpecification();
        return afishaService.query(findAll);
    }

    @GetMapping(value = "", params = {"title"})
    public List<Afisha> findAfishasByTitle(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindByTitleAfishaSpecification findByTitle = new FindByTitleAfishaSpecification(request.getParameter("title"));
        return afishaService.query(findByTitle);
    }

    @GetMapping(value = "/{id}")
    public Afisha findAfishaById(@PathVariable int id, HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindByIdAfishaSpecification findById = new FindByIdAfishaSpecification(id);
        List<Afisha> findByIdAfishaList = afishaService.query(findById);
        if (findByIdAfishaList.isEmpty()) {
            throw new InstanceNotFoundException();
        }
        return findByIdAfishaList.get(0);
    }

    @PostMapping(value = "")
    public void createAfisha(@RequestBody Afisha afisha, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            afishaService.create(afisha);
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @PutMapping(value = "/{id}")
    public void updateAfisha(@RequestBody Afisha afisha, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            afishaService.update(afisha);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @DeleteMapping(value = "/{id}")
    public void deleteAfisha(@PathVariable int id, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            afishaService.delete(id);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }
}
