package news.dao.specifications;

import news.model.Article;

public class FindAllArticleSpecification implements ExtendSqlSpecification<Article> {

    @Override
    public boolean isSpecified(Article article) {
        return true;
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM ( " +
                "   SELECT article.id as new_id, * FROM article" +
                "   LEFT JOIN image ON article.id = image.article_id" +
                "   LEFT JOIN article_tag ON article_tag.article_id=0" +
                "UNION" +
                "   SELECT article.id as new_id, * FROM article" +
                "   LEFT JOIN image ON image.article_id = 0" +
                "   LEFT JOIN article_tag ON article.id = article_tag.article_id" +
                ") result " +
                "ORDER BY result.new_id, 12, 16";
    }

    @Override
    public boolean isById() {
        return false;
    }

    @Override
    public Object getCriterial() {
        return null;
    }
}
