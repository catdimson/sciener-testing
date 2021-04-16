package news.dao.specifications;

import news.model.Source;

public class FindByTitleSourceSpecification implements SqlSpecification<Source> {
    final private String title;

    public FindByTitleSourceSpecification(String title) {
        this.title = title;
    }

    @Override
    public boolean isSpecified(Source source) {
        return source.getObjects()[1] == this.title;
    }

    @Override
    public String toSqlClauses() {
        return String.format("SELECT * FROM source WHERE title='%s';", this.title);
    }

    @Override
    public Object getCriterial() {
        return this.title;
    }
}
