package news.dao.specifications;

import news.model.Article;

public class FindByTitleArticleSpecification implements ExtendSqlSpecification<Article> {
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
        return "SELECT * FROM ( " +
                "   SELECT article.id as new_id, * FROM article" +
                "   LEFT JOIN image ON article.id = image.article_id" +
                "   LEFT JOIN article_tag ON article_tag.article_id=0" +
                "   WHERE article.title=?" +
                "UNION" +
                "   SELECT article.id as new_id, * FROM article" +
                "   LEFT JOIN image ON image.article_id = 0" +
                "   LEFT JOIN article_tag ON article.id = article_tag.article_id" +
                "   WHERE article.title = ?" +
                ") result " +
                "ORDER BY result.new_id, 12, 16";
    }

    @Override
    public boolean isById() {
        return false;
    }

    @Override
    public Object getCriterial() {
        return this.title;
    }
}


/*SELECT * FROM (

        SELECT article.id as new_id, * FROM article
        LEFT JOIN image ON article.id = image.article_id
        LEFT JOIN article_tag ON article_tag.article_id=0
        WHERE article.title='title_1'

        UNION

        SELECT article.id as new_id, * FROM article
        LEFT JOIN image ON image.article_id = 0
        LEFT JOIN article_tag ON article.id = article_tag.article_id
        WHERE article.title = 'title_1'

        ) result

        ORDER BY result.new_id*/
