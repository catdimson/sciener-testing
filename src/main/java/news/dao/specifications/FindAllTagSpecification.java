package news.dao.specifications;

import news.model.Tag;

public class FindAllTagSpecification implements ExtendSqlSpecification<Tag> {

    @Override
    public boolean isSpecified(Tag category) {
        return true;
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM tag;";
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
