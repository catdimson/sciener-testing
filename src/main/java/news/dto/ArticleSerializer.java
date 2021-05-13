package news.dto;

import news.model.Article;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Override
    public String toJSON() {
        String[] articleFields = Article.getFields();
        String[] articleImageFields = Article.ArticleImage.getFields();
        Object[] articleInstance = article.getObjects();
        LocalDate createDate = (LocalDate) articleInstance[3];
        LocalDate editDate = (LocalDate) articleInstance[4];

        return "" +
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
            serializeImagesToJSON(articleImageFields, articleInstance[10]) +
            "\t" + "],\n" +
            "\t" + "\"" + articleFields[11] + "\"" + ":" + "[\n" +
            serializeTagsIdToJSON(articleInstance[11]) +
            "\t" + "]\n" +
            "}";
    }

    @Override
    public Article toObject() {
        int id = 0;
        String title;
        String lead;
        LocalDate createDate;
        LocalDate editDate;
        String text;
        boolean isPublished = false;
        int categoryId;
        int userId;
        int sourceId;
        int indexLine = 1;
        boolean withId;

        String[] lines = json.split("\n");
        /*for (int i = 0; i < lines.length; i++) {
            System.out.println(i + ":" + lines[i]);
        }*/
        Pattern p = Pattern.compile("\"id\":.+");
        Matcher m = p.matcher(lines[indexLine]);
        withId = m.find();
        if (withId) {
            p = Pattern.compile(":(\\d+),");
            m = p.matcher(lines[indexLine]);
            m.find();
            id = Integer.parseInt(m.group(1));
            indexLine++;
        }
        // title
        m = Pattern.compile(":\"(.+)\",").matcher(lines[indexLine]); // 1/2
        m.find();
        title = m.group(1);
        indexLine++;
        // lead
        m = Pattern.compile(":\"(.+)\",").matcher(lines[indexLine]); // 2/3
        m.find();
        lead = m.group(1);
        indexLine++;
        // createDate
        m = Pattern.compile(":(\\d+),").matcher(lines[indexLine]); // 3/4
        m.find();
        int timestampCreateDate = Integer.parseInt(m.group(1));
        createDate = Timestamp.from(Instant.ofEpochSecond(timestampCreateDate)).toLocalDateTime().toLocalDate();
        indexLine++;
        // editDate
        m = Pattern.compile(":(\\d+),").matcher(lines[indexLine]); // 4/5
        m.find();
        int timestampEditDate = Integer.parseInt(m.group(1));
        editDate = Timestamp.from(Instant.ofEpochSecond(timestampEditDate)).toLocalDateTime().toLocalDate();
        indexLine++;
        // text
        m = Pattern.compile(":\"(.+)\",").matcher(lines[indexLine]); // 5/6
        m.find();
        text = m.group(1);
        indexLine++;
        // isPublished
        m = Pattern.compile(":(\\w{4,5}),").matcher(lines[indexLine]); // 6/7
        if (m.find()) {
            if (m.group(1).equals("true")) {
                isPublished = true;
            }
            if (m.group(1).equals("false")) {
                isPublished = false;
            }
        }
        indexLine++;
        // categoryId
        m = Pattern.compile(":(\\d+),").matcher(lines[indexLine]); // 7/8
        m.find();
        categoryId = Integer.parseInt(m.group(1));
        indexLine++;
        // userId
        m = Pattern.compile(":(\\d+),").matcher(lines[indexLine]); // 8/9
        m.find();
        userId = Integer.parseInt(m.group(1));
        indexLine++;
        // sourceId
        m = Pattern.compile(":(\\d+),").matcher(lines[indexLine]); // 9/10
        m.find();
        sourceId = Integer.parseInt(m.group(1));
        // создаем по распарсеным данным объект статьи
        Article article;
        if (withId) {
            article = new Article(id, title, lead, createDate, editDate, text, isPublished, categoryId, userId, sourceId);
        } else {
            article = new Article(title, lead, createDate, editDate, text, isPublished, categoryId, userId, sourceId);
        }

        // если прикрепленных изображений нет
        if (Pattern.compile("]").matcher(lines[indexLine + 2]).find()) {
            // если не указаны теги
            if (Pattern.compile("]").matcher(lines[indexLine + 4]).find()) {
                System.out.println("Тегов нет");
            // если теги указаны
            } else {
                int indexEndTag = indexLine + 4;
                while (true) {
                    Matcher localMatcher = Pattern.compile("(\\d+)").matcher(lines[indexEndTag]);
                    localMatcher.find();
                    int idTag = Integer.parseInt(m.group(1));
                    article.addNewTagId(idTag);
                    indexEndTag += 1;
                    if (!Pattern.compile("(\\d+)").matcher(lines[indexEndTag]).find()) {
                        return article;
                    }
                }
            }
        // если прикрепленные изображения есть
        } else {
            int indexEndImages = indexLine + 3; // 12/13

            // извлекаем все изображения
            while (true) {
                int idImage = 0;
                String titleImage;
                String pathImage;
                int articleIdImage;
                // id
                if (withId) {
                    Matcher m1 = Pattern.compile(":(\\d+),").matcher(lines[indexEndImages]);
                    m1.find();
                    idImage = Integer.parseInt(m1.group(1));
                    indexEndImages += 1;
                }

                // title
                Matcher m2 = Pattern.compile(":\"(.+)\",").matcher(lines[indexEndImages]);
                m2.find();
                titleImage = m2.group(1);
                indexEndImages += 1;

                // path
                Matcher m3 = Pattern.compile(":\"(.+)\",").matcher(lines[indexEndImages]);
                m3.find();
                pathImage = m3.group(1);
                indexEndImages += 1;

                // articleId
                Matcher m4 = Pattern.compile(":(\\d+)").matcher(lines[indexEndImages]);
                m4.find();
                articleIdImage = Integer.parseInt(m4.group(1));
                indexEndImages += 3;

                // создаем изображение и добавляем в статью
                Article.ArticleImage articleImage;
                if (withId) {
                    articleImage = new Article.ArticleImage(idImage, titleImage, pathImage, articleIdImage);
                } else {
                    articleImage = new Article.ArticleImage(titleImage, pathImage, articleIdImage);
                }
                article.addNewImage(articleImage);

                if (Pattern.compile("tagsId").matcher(lines[indexEndImages]).find()) {
                    break;
                }
            }

            // добавляем все теги
            int indexEndTag = indexEndImages + 1;
            while (true) {
                Matcher localMatcher = Pattern.compile("(\\d+)").matcher(lines[indexEndTag]);
                localMatcher.find();
                int idTag = Integer.parseInt(localMatcher.group(1));
                article.addNewTagId(idTag);
                indexEndTag += 1;
                if (!Pattern.compile("(\\d+)").matcher(lines[indexEndTag]).find()) {
                    return article;
                }
            }
        }

        return article;
    }

    private String serializeTagsIdToJSON(Object tagsId) {
        List tags = new ArrayList((HashSet) tagsId);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            Object tagId = tags.get(i);
            result.append("\t\t").append(tagId);
            if (i != tags.size() - 1) {
                result.append(",\n");
            } else {
                result.append("\n");
            }
        }
        return result.toString();
    }

    private String serializeImagesToJSON(String[] imageFields, Object images) {
        List listImageObjects = (ArrayList) images;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < listImageObjects.size(); i++) {
            Article.ArticleImage image = (Article.ArticleImage) listImageObjects.get(i);
            Object[] imageInstance = image.getObjects();
            String imageString = "" +
                    "\t\t" + "{\n" +
                    "\t\t\t" + "\"" + imageFields[0] + "\"" + ":" + imageInstance[0] + ",\n" +
                    "\t\t\t" + "\"" + imageFields[1] + "\"" + ":" + "\"" + imageInstance[1] + "\"" + ",\n" +
                    "\t\t\t" + "\"" + imageFields[2] + "\"" + ":" + "\"" + imageInstance[2] + "\"" + ",\n" +
                    "\t\t\t" + "\"" + imageFields[3] + "\"" + ":" + imageInstance[3] + "\n" +
                    "\t\t";
            if (i != listImageObjects.size() - 1) {
                imageString += "},\n";
            } else {
                imageString += "}\n";
            }
            result.append(imageString);
        }

        return result.toString();
    }
}
