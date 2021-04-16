package news.dao.specifications;

import news.model.Tag;

public class FindByTitleTagSpecification implements SqlSpecification<Tag> {
    final private String title;

    public FindByTitleTagSpecification(String title) {
        this.title = title;
    }

    @Override
    public boolean isSpecified(Tag category) {
        return category.getObjects()[1] == this.title;
    }

    @Override
    public String toSqlClauses() {
        return String.format("SELECT * FROM tag WHERE title='%s';", this.title);
    }
}
