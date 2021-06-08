package news.web.controllers;

import news.dao.specifications.FindAllUserSpecification;
import news.dao.specifications.FindByFirstnameUserSpecification;
import news.dao.specifications.FindByIdUserSpecification;
import news.model.User;
import news.service.UserService;
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
@RequestMapping(value = "/user")
public class UserController {
    UserService userService;

    public UserController() {
    }

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "")
    public List<User> findAllUsers(HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindAllUserSpecification findAll = new FindAllUserSpecification();
        return userService.query(findAll);
    }

    @GetMapping(value = "", params = {"firstname"})
    public List<User> findUsersByFirstname(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindByFirstnameUserSpecification findByFirstname = new FindByFirstnameUserSpecification(request.getParameter("firstname"));
        return userService.query(findByFirstname);
    }

    @GetMapping(value = "/{id}")
    public User findUserById(@PathVariable int id, HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindByIdUserSpecification findById = new FindByIdUserSpecification(id);
        List<User> findByIdUserList = userService.query(findById);
        if (findByIdUserList.isEmpty()) {
            throw new InstanceNotFoundException();
        }
        return findByIdUserList.get(0);
    }

    @PostMapping(value = "")
    public void createUser(@RequestBody User user, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            userService.create(user);
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @PutMapping(value = "/{id}")
    public void updateUser(@RequestBody User user, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            userService.update(user);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @DeleteMapping(value = "/{id}")
    public void deleteUser(@PathVariable int id, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            userService.delete(id);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }
}
