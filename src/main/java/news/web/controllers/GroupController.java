package news.web.controllers;

import news.dao.specifications.FindAllGroupSpecification;
import news.dao.specifications.FindByIdGroupSpecification;
import news.dao.specifications.FindByTitleGroupSpecification;
import news.model.Group;
import news.service.GroupService;
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
@RequestMapping(value = "/group")
public class GroupController {
    GroupService groupService;

    public GroupController() {
    }

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping(value = "")
    public List<Group> findAllGroups(HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindAllGroupSpecification findAll = new FindAllGroupSpecification();
        return groupService.query(findAll);
    }

    @GetMapping(value = "", params = {"title"})
    public List<Group> findGroupsByTitle(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindByTitleGroupSpecification findByTitle = new FindByTitleGroupSpecification(request.getParameter("title"));
        return groupService.query(findByTitle);
    }

    @GetMapping(value = "/{id}")
    public Group findGroupById(@PathVariable int id, HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindByIdGroupSpecification findById = new FindByIdGroupSpecification(id);
        List<Group> findByIdGroupList = groupService.query(findById);
        if (findByIdGroupList.isEmpty()) {
            throw new InstanceNotFoundException();
        }
        return findByIdGroupList.get(0);
    }

    @PostMapping(value = "")
    public void createGroup(@RequestBody Group group, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            groupService.create(group);
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @PutMapping(value = "/{id}")
    public void updateGroup(@RequestBody Group group, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            groupService.update(group);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @DeleteMapping(value = "/{id}")
    public void deleteGroup(@PathVariable int id, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            groupService.delete(id);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }
}
