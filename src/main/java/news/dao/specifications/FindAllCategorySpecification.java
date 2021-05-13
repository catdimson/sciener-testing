package news.dao.specifications;

import news.model.Category;

public class FindAllCategorySpecification implements ExtendSqlSpecification<Category> {

    @Override
    public boolean isSpecified(Category category) {
        return true;
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM category;";
    }

    @Override
    public Object getCriterial() {
        return null;
    }

    @Override
    public boolean isById() {
        return false;
    }
}
