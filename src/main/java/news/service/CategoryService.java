package news.service;

import news.dao.repositories.CategoryRepository;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Category;

import java.sql.SQLException;
import java.util.List;

public class CategoryService implements Service<Category> {
    final private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> query(ExtendSqlSpecification<Category> specification) throws SQLException {
        return categoryRepository.query(specification);
    }

    @Override
    public int create(Category instance) throws SQLException {
        return categoryRepository.create(instance);
    }

    @Override
    public int delete(int id) throws SQLException {
        return categoryRepository.delete(id);
    }

    @Override
    public int update(Category instance) throws SQLException {
        return categoryRepository.update(instance);
    }
}
