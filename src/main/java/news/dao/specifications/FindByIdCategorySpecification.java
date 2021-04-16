package news.dao.specifications;

import news.model.Category;

public class FindByIdCategorySpecification implements SqlSpecification<Category> {
    final private int id;

    public FindByIdCategorySpecification(int id) {
        this.id = id;
    }

    @Override
    public boolean isSpecified(Category category) {
        return (int) category.getObjects()[0] == this.id;
    }

    @Override
    public String toSqlClauses() {
        return String.format("SELECT * FROM category WHERE id='%d';", this.id);
    }

    @Override
    public Object getCriterial() {
        return this.id;
    }
}
