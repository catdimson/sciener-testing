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
                "SELECT * FROM article" +
                "    LEFT JOIN image ON article.id = image.article_id" +
                "    LEFT JOIN article_tag ON article_tag.article_id=0" +
                "    WHERE article.id=%s " +
                "UNION" +
                "    SELECT * FROM article" +
                "    LEFT JOIN image ON image.article_id=0" +
                "    LEFT JOIN article_tag ON article.id=article_tag.article_id" +
                "    WHERE article.id=%s;", this.id, this.id);
    }

    public boolean isById() {
        return true;
    }
}
