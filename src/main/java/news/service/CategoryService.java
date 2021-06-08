package news.service;

import news.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<Category> findAll();

    List<Category> findByTitle(String title);

    Optional<Category> findById(int id);

    Category createCategory(Category category);

    Category updateCategory(Category category);

    void deleteCategory(int id);

}
