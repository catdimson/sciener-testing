package news.web.controllers;

import news.model.Group;
import news.service.GroupService;
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
@RequestMapping(value = "/group")
public class GroupController {
    GroupService groupService;

    public GroupController() {
    }

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping(value = "/")
    public List<Group> findAllGroups(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        return groupService.findAll();
    }

    @GetMapping(value = "/", params = {"title"})
    public List<Group> findGroupsByTitle(HttpServletRequest request, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        return groupService.findByTitle(request.getParameter("title"));
    }

    @GetMapping(value = "/{id}/")
    public Optional<Group> findGroupById(@PathVariable int id, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        Optional<Group> group = groupService.findById(id);
        if (group.isEmpty()) {
            throw new InstanceNotFoundException();
        }
        return group;
    }

    @PostMapping(value = "/")
    public void createGroup(@RequestBody Group group, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            groupService.createGroup(group);
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @PutMapping(value = "/{id}/")
    public void updateGroup(@RequestBody Group group, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            groupService.updateGroup(group);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @DeleteMapping(value = "/{id}/")
    public void deleteGroup(@PathVariable int id, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            groupService.deleteGroup(id);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }
}
