package news.dao.specifications;

import news.model.Afisha;

public class FindByTitleAfishaSpecification implements ExtendSqlSpecification<Afisha> {
    final private String title;

    public FindByTitleAfishaSpecification(String title) {
        this.title = title;
    }

    @Override
    public boolean isSpecified(Afisha afisha) {
        return afisha.equalsWithTitle(this.title);
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM afisha WHERE title=?;";
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
