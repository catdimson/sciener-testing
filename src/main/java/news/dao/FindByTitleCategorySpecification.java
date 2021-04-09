package news.dao;

import news.model.Category;

public class FindByTitleCategorySpecification implements Specification<Category> {
    private final String title;

    public FindByTitleCategorySpecification(String title) {
        this.title = title;
    }

    @Override
    public boolean isSpecified(Category category) {
        return this.title.equals(category.getTitle());
    }
}
