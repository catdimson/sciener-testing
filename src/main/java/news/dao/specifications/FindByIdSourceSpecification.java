package news.dao.specifications;

import news.model.Source;

public class FindByIdSourceSpecification implements ExtendSqlSpecification<Source> {
    final private int id;

    public FindByIdSourceSpecification(int id) {
        this.id = id;
    }

    @Override
    public boolean isSpecified(Source source) {
        return source.equalsWithId(this.id);
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM source WHERE id=?;";
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
