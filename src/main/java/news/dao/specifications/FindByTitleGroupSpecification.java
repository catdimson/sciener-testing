package news.dao.specifications;

import news.model.Group;

public class FindByTitleGroupSpecification implements SqlSpecification<Group> {
    final private String title;

    public FindByTitleGroupSpecification(String title) {
        this.title = title;
    }

    @Override
    public boolean isSpecified(Group group) {
        return group.getObjects()[1] == this.title;
    }

    @Override
    public String toSqlClauses() {
        return String.format("SELECT * FROM \"group\" WHERE title='%s';", this.title);
    }
}
