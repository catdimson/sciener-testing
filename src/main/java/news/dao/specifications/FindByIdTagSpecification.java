package news.dao.specifications;

import news.model.Tag;

public class FindByIdTagSpecification implements ExtendSqlSpecification<Tag> {
    final private int id;

    public FindByIdTagSpecification(int id) {
        this.id = id;
    }

    @Override
    public boolean isSpecified(Tag tag) {
        return tag.equalsWithId(this.id);
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM tag WHERE id=?;";
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
