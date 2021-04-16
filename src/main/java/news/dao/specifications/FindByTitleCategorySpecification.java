package news.dao.specifications;

import news.model.Category;

public class FindByTitleCategorySpecification implements ExtendSqlSpecification<Category> {
    final private String title;

    public FindByTitleCategorySpecification(String title) {
        this.title = title;
    }

    @Override
    public boolean isSpecified(Category category) {
        return category.equalsWithTitle(this.title);
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM category WHERE title=?;";
    }

    @Override
    public Object getCriterial() {
        return this.title;
    }

    @Override
    public boolean isById() {
        return false;
    }
}
