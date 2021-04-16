package news.dao.specifications;

import news.model.Source;

public class FindByTitleSourceSpecification implements ExtendSqlSpecification<Source> {
    final private String title;

    public FindByTitleSourceSpecification(String title) {
        this.title = title;
    }

    @Override
    public boolean isSpecified(Source source) {
        return source.equalsWithTitle(this.title);
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM source WHERE title=?;";
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
