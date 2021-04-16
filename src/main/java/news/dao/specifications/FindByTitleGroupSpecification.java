package news.dao.specifications;

import news.model.Group;

public class FindByTitleGroupSpecification implements ExtendSqlSpecification<Group> {
    final private String title;

    public FindByTitleGroupSpecification(String title) {
        this.title = title;
    }

    @Override
    public boolean isSpecified(Group group) {
        return group.equalsWithTitle(this.title);
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM \"group\" WHERE title=?;";
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
