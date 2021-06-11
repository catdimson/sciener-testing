package news.web.controllers;

import news.model.Mailing;
import news.service.MailingService;
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
    public List<Mailing> findAllMailings(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        return mailingService.findAll();
    }

    @GetMapping(value = "", params = {"email"})
    public List<Mailing> findMailingsByTitle(HttpServletRequest request, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        return mailingService.findByEmail(request.getParameter("email"));
    }

    @GetMapping(value = "/{id}")
    public Optional<Mailing> findMailingById(@PathVariable int id, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        Optional<Mailing> mailing = mailingService.findById(id);
        if (mailing.isEmpty()) {
            throw new InstanceNotFoundException();
        }
        return mailing;
    }

    @PostMapping(value = "")
    public void createMailing(@RequestBody Mailing mailing, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            mailingService.createMailing(mailing);
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @PutMapping(value = "/{id}")
    public void updateMailing(@RequestBody Mailing mailing, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            mailingService.updateMailing(mailing);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @DeleteMapping(value = "/{id}")
    public void deleteMailing(@PathVariable int id, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            mailingService.deleteMailing(id);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }
}
