package news.dao.specifications;

import news.model.Source;

public class FindByIdSourceSpecification implements SqlSpecification<Source> {
    final private int id;

    public FindByIdSourceSpecification(int id) {
        this.id = id;
    }

    @Override
    public boolean isSpecified(Source source) {
        return (int) source.getObjects()[0] == this.id;
    }

    @Override
    public String toSqlClauses() {
        return String.format("SELECT * FROM source WHERE id='%d';", this.id);
    }

    @Override
    public Object getCriterial() {
        return this.id;
    }
}
