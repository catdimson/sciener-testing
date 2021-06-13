package news.web.controllers;

import news.model.User;
import news.service.UserService;
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
@RequestMapping(value = "/user")
public class UserController {
    UserService userService;

    public UserController() {
    }

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/")
    public List<User> findAllUsers(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        return userService.findAll();
    }

    @GetMapping(value = "/", params = {"firstname"})
    public List<User> findUsersByFirstname(HttpServletRequest request, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        return userService.findByFirstName(request.getParameter("firstname"));
    }

    @GetMapping(value = "/{id}/")
    public Optional<User> findUserById(@PathVariable int id, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        Optional<User> user = userService.findById(id);
        if (user.isEmpty()) {
            throw new InstanceNotFoundException();
        }
        return user;
    }

    @PostMapping(value = "/")
    public void createUser(@RequestBody User user, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            userService.createUser(user);
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @PutMapping(value = "/{id}/")
    public void updateUser(@RequestBody User user, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            userService.updateUser(user);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @DeleteMapping(value = "/{id}/")
    public void deleteUser(@PathVariable int id, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            userService.deleteUser(id);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }
}
