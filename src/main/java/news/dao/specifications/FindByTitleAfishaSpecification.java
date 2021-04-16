package news.dao.specifications;

import news.model.Afisha;

public class FindByTitleAfishaSpecification implements SqlSpecification<Afisha> {
    final private String title;

    public FindByTitleAfishaSpecification(String title) {
        this.title = title;
    }

    @Override
    public boolean isSpecified(Afisha afisha) {
        return afisha.getObjects()[1] == this.title;
    }

    @Override
    public String toSqlClauses() {
        return String.format("SELECT * FROM afisha WHERE title='%s';", this.title);
    }
}
