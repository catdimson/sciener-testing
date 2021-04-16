package news.dao.specifications;

import news.model.Tag;

public class FindByIdTagSpecification implements SqlSpecification<Tag> {
    final private int id;

    public FindByIdTagSpecification(int id) {
        this.id = id;
    }

    @Override
    public boolean isSpecified(Tag tag) {
        return (int) tag.getObjects()[0] == this.id;
    }

    @Override
    public String toSqlClauses() {
        return String.format("SELECT * FROM tag WHERE id='%d';", this.id);
    }

    @Override
    public Object getCriterial() {
        return this.id;
    }
}
