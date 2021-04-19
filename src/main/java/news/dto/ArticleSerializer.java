package news.dto;

import news.model.Article;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Класс сериализации статьи в JSON
 */
public class ArticleSerializer implements Serializer<Article> {
    private String json;
    private Article article;

    public ArticleSerializer(Article article) {
        this.article = article;
    }

    public ArticleSerializer(String json) {
        this.json = json;
    }

    String test = "{\n" +
            "\t     \"id\"           :   1,\n" +
            "\t     \"title\"        :   \"заголовок\",\n" +
            "\t     \"lead\"         :   \"лид\",\n" +
            "\t     \"createDate\"   :   \"дата создания\",\n" +
            "\t     \"editDate\"     :   \"дата редактирования\",\n" +
            "\t     \"text\"         :   \"содержимое статьи\",\n" +
            "\t     \"isPublished\"  :   true,\n" +
            "\t     \"categoryId\"   :   1,\n" +
            "\t     \"userId\"       :   1,\n" +
            "\t     \"sourceId\"     :   1,\n" +
            "\t     \"images\"       :   [\n" +
            "\t\t      {\n" +
            "\t\t\t        \"id\"           :  1,\n" +
            "\t\t\t        \"title\"        :  \"название изображения\",\n" +
            "\t\t\t        \"path\"         :  \"путь\",\n" +
            "\t\t\t        \"articleId\"    :  1\n" +
            "\t\t      },\n" +
            "]\n," +
            "\t     \"tagsID\"       :   [1,2,3,4,5]\n" +
            "}";

    @Override
    public String toJSON() {
        // получение имен полей
        String[] articleFields = Article.getFields();
        String[] articleImageFields = Article.ArticleImage.getFields();
        // получаем значения полей статьи
        Object[] articleInstance = article.getObjects();
        LocalDate createDate = (LocalDate) articleInstance[3];
        LocalDate editDate = (LocalDate) articleInstance[4];

        // создаем строку, которая будет представлять результат работы метода
        String result = "";
        result = "" +
                "{\n" +
                "\t" + "\"" + articleFields[0] + "\"" + ":" + articleInstance[0] + ",\n" +
                "\t" + "\"" + articleFields[1] + "\"" + ":" + "\"" + articleInstance[1] + "\"" + ",\n" +
                "\t" + "\"" + articleFields[2] + "\"" + ":" + "\"" + articleInstance[2] + "\"" + ",\n" +
                "\t" + "\"" + articleFields[3] + "\"" + ":" + Timestamp.valueOf(createDate.atStartOfDay()).getTime() / 1000 + ",\n" +
                "\t" + "\"" + articleFields[4] + "\"" + ":" + Timestamp.valueOf(editDate.atStartOfDay()).getTime() / 1000 + ",\n" +
                "\t" + "\"" + articleFields[5] + "\"" + ":" + "\"" + articleInstance[5] + "\"" + ",\n" +
                "\t" + "\"" + articleFields[6] + "\"" + ":" + articleInstance[6] + ",\n" +
                "\t" + "\"" + articleFields[7] + "\"" + ":" + articleInstance[7] + ",\n" +
                "\t" + "\"" + articleFields[8] + "\"" + ":" + articleInstance[8] + ",\n" +
                "\t" + "\"" + articleFields[9] + "\"" + ":" + articleInstance[9] + ",\n" +
                "\t" + "\"" + articleFields[10] + "\"" + ":" + "[\n" +
                serializeImages(articleImageFields, articleInstance[10]) +
                "\t" + "],\n" +
                "\t" + "\"" + articleFields[11] + "\"" + ":" + "[\n" +
                serializeTagsId(articleInstance[11]) +
                "\t" + "]\n" +
                "}";

        return result.toString();
    }

    private String serializeTagsId(Object tagsId) {
        List tags = new ArrayList((HashSet) tagsId);
        StringBuilder result = new StringBuilder();
        for (Object tagId : tags) {
            result.append("\t\t").append(tagId).append(",\n");
        }
        return result.toString();
    }

    private String serializeImages(String[] imageFields, Object images) {
        List listImageObjects = (ArrayList) images;
        StringBuilder result = new StringBuilder();
        for (Object imageObject : listImageObjects) {
            Article.ArticleImage image = (Article.ArticleImage) imageObject;
            Object[] imageInstance = image.getObjects();
            String imageString = "" +
                    "\t\t" + "{\n" +
                    "\t\t\t" + "\"" + imageFields[0] + "\"" + ":" + imageInstance[0] + ",\n" +
                    "\t\t\t" + "\"" + imageFields[1] + "\"" + ":" + "\"" + imageInstance[1] + "\"" + ",\n" +
                    "\t\t\t" + "\"" + imageFields[2] + "\"" + ":" + "\"" + imageInstance[2] + "\"" + ",\n" +
                    "\t\t\t" + "\"" + imageFields[3] + "\"" + ":" + imageInstance[3] + ",\n" +
                    "\t\t" + "},\n";
            result.append(imageString);
        }
        return result.toString();
    }

    @Override
    public Article toObject() {
        return null;
    }
}
