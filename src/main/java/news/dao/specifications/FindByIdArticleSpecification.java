package news.dao.specifications;

import news.model.Article;

public class FindByIdArticleSpecification implements SqlSpecification<Article> {
    final private int id;

    public FindByIdArticleSpecification(int id) {
        this.id = id;
    }

    @Override
    public boolean isSpecified(Article article) {
        return (int) article.getObjects()[0] == this.id;
    }

    @Override
    public String toSqlClauses() {
        return String.format("" +
                "SELECT * FROM article WHERE id='%d';", this.id);
    }
}
