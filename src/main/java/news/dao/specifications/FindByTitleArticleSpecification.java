package news.dao.specifications;

import news.model.Article;

public class FindByTitleArticleSpecification implements SqlSpecification<Article> {
    final private String title;

    public FindByTitleArticleSpecification(String title) {
        this.title = title;
    }

    @Override
    public boolean isSpecified(Article article) {
        return article.getObjects()[1] == this.title;
    }

    @Override
    public String toSqlClauses() {
        return String.format("SELECT * FROM article WHERE title='%s';", this.title);
    }
}
