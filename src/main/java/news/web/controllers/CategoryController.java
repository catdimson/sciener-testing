package news.web.controllers;

import news.model.Category;
import news.service.CategoryService;
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
@RequestMapping(value = "/category")
public class CategoryController {
    CategoryService categoryService;

    public CategoryController() {
    }

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping(value = "/")
    public List<Category> findAllCategorys(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        return categoryService.findAll();
    }

    @GetMapping(value = "/", params = {"title"})
    public List<Category> findCategorysByTitle(HttpServletRequest request, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        return categoryService.findByTitle(request.getParameter("title"));
    }

    @GetMapping(value = "/{id}/")
    public Optional<Category> findCategoryById(@PathVariable int id, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        Optional<Category> category = categoryService.findById(id);
        if (category.isEmpty()) {
            throw new InstanceNotFoundException();
        }
        return category;
    }

    @PostMapping(value = "/")
    public void createCategory(@RequestBody Category category, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            categoryService.createCategory(category);
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @PutMapping(value = "/{id}/")
    public void updateCategory(@RequestBody Category category, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            categoryService.updateCategory(category);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @DeleteMapping(value = "/{id}/")
    public void deleteCategory(@PathVariable int id, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            categoryService.deleteCategory(id);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }
}
