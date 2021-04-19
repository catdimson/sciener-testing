package news.dao.specifications;

import news.model.Group;

public class FindByIdGroupSpecification implements ExtendSqlSpecification<Group> {
    final private int id;

    public FindByIdGroupSpecification(int id) {
        this.id = id;
    }

    public boolean isSpecified(Group group) {
        return group.equalsWithId(this.id);
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM \"group\" WHERE id=?;";
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
