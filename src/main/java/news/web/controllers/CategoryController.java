package news.web.controllers;

import news.dao.specifications.FindAllCategorySpecification;
import news.dao.specifications.FindByIdCategorySpecification;
import news.dao.specifications.FindByTitleCategorySpecification;
import news.model.Category;
import news.service.CategoryService;
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
@RequestMapping(value = "/category")
public class CategoryController {
    CategoryService categoryService;

    public CategoryController() {
    }

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping(value = "")
    public List<Category> findAllCategorys(HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindAllCategorySpecification findAll = new FindAllCategorySpecification();
        return categoryService.query(findAll);
    }

    @GetMapping(value = "", params = {"title"})
    public List<Category> findCategorysByTitle(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindByTitleCategorySpecification findByTitle = new FindByTitleCategorySpecification(request.getParameter("title"));
        return categoryService.query(findByTitle);
    }

    @GetMapping(value = "/{id}")
    public Category findCategoryById(@PathVariable int id, HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindByIdCategorySpecification findById = new FindByIdCategorySpecification(id);
        List<Category> findByIdCategoryList = categoryService.query(findById);
        if (findByIdCategoryList.isEmpty()) {
            throw new InstanceNotFoundException();
        }
        return findByIdCategoryList.get(0);
    }

    @PostMapping(value = "")
    public void createCategory(@RequestBody Category category, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            categoryService.create(category);
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @PutMapping(value = "/{id}")
    public void updateCategory(@RequestBody Category category, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            categoryService.update(category);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @DeleteMapping(value = "/{id}")
    public void deleteCategory(@PathVariable int id, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            categoryService.delete(id);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }
}
