package news.web.controllers;

import news.dao.specifications.FindAllMailingSpecification;
import news.dao.specifications.FindByEmailMailingSpecification;
import news.dao.specifications.FindByIdMailingSpecification;
import news.model.Mailing;
import news.service.MailingService;
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
@RequestMapping(value = "/mailing")
public class MailingController {
    MailingService mailingService;

    public MailingController() {
    }

    @Autowired
    public MailingController(MailingService mailingService) {
        this.mailingService = mailingService;
    }

    @GetMapping(value = "")
    public List<Mailing> findAllMailings(HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindAllMailingSpecification findAll = new FindAllMailingSpecification();
        return mailingService.query(findAll);
    }

    @GetMapping(value = "", params = {"email"})
    public List<Mailing> findMailingsByTitle(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindByEmailMailingSpecification findByTitle = new FindByEmailMailingSpecification(request.getParameter("email"));
        return mailingService.query(findByTitle);
    }

    @GetMapping(value = "/{id}")
    public Mailing findMailingById(@PathVariable int id, HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindByIdMailingSpecification findById = new FindByIdMailingSpecification(id);
        List<Mailing> findByIdMailingList = mailingService.query(findById);
        if (findByIdMailingList.isEmpty()) {
            throw new InstanceNotFoundException();
        }
        return findByIdMailingList.get(0);
    }

    @PostMapping(value = "")
    public void createMailing(@RequestBody Mailing mailing, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            mailingService.create(mailing);
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @PutMapping(value = "/{id}")
    public void updateMailing(@RequestBody Mailing mailing, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            mailingService.update(mailing);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @DeleteMapping(value = "/{id}")
    public void deleteMailing(@PathVariable int id, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            mailingService.delete(id);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }
}
