package news.dao.specifications;

import news.model.Category;

public class FindByIdCategorySpecification implements ExtendSqlSpecification<Category> {
    final private int id;

    public FindByIdCategorySpecification(int id) {
        this.id = id;
    }

    @Override
    public boolean isSpecified(Category category) {
        return category.equalsWithId(this.id);
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM category WHERE id=?;";
    }

    @Override
    public Object getCriterial() {
        return this.id;
    }

    @Override
    public boolean isById() {
        return true;
    }
}
