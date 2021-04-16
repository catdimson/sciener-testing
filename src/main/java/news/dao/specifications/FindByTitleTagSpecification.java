package news.dao.specifications;

import news.model.Tag;

public class FindByTitleTagSpecification implements ExtendSqlSpecification<Tag> {
    final private String title;

    public FindByTitleTagSpecification(String title) {
        this.title = title;
    }

    @Override
    public boolean isSpecified(Tag category) {
        return category.equalsWithTitle(this.title);
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM tag WHERE title=?;";
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
