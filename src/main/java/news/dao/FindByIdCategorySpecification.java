package news.dao;

import news.model.Category;

public class FindByIdCategorySpecification implements Specification<Category> {
    final private int id;

    public FindByIdCategorySpecification(int id) {
        this.id = id;
    }

    @Override
    public boolean isSpecified(Category category) {
        return this.id == category.getUniqNumber();
    }
}
