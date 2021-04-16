package news.dao.specifications;

import news.model.Group;

public class FindByIdGroupSpecification implements SqlSpecification<Group> {
    final private int id;

    public FindByIdGroupSpecification(int id) {
        this.id = id;
    }

    @Override
    public boolean isSpecified(Group group) {
        return (int) group.getObjects()[0] == this.id;
    }

    @Override
    public String toSqlClauses() {
        return String.format("SELECT * FROM \"group\" WHERE id='%d';", this.id);
    }
}
