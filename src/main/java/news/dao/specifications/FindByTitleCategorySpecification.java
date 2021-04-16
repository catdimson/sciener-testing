package news.dao.specifications;

import news.model.Category;

public class FindByTitleCategorySpecification implements SqlSpecification<Category> {
    final private String title;

    public FindByTitleCategorySpecification(String title) {
        this.title = title;
    }

    @Override
    public boolean isSpecified(Category category) {
        return category.getObjects()[1] == this.title;
    }

    @Override
    public String toSqlClauses() {
        return String.format("SELECT * FROM category WHERE title='%s';", this.title);
    }

    @Override
    public Object getCriterial() {
        return this.title;
    }
}
