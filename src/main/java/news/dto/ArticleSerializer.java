package news.dto;

import news.model.Article;
import news.model.ArticleImage;
import news.model.Tag;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
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
        String[] tagFields = Tag.getFields();
        String[] articleImageFields = ArticleImage.getFields();
        Object[] articleInstance = article.getObjects();
        Timestamp createDate = (Timestamp) articleInstance[3];
        Timestamp editDate = (Timestamp) articleInstance[4];

        return "" +
            "{\n" +
            "\t" + "\"" + articleFields[0] + "\"" + ":" + articleInstance[0] + ",\n" +
            "\t" + "\"" + articleFields[1] + "\"" + ":" + "\"" + articleInstance[1] + "\"" + ",\n" +
            "\t" + "\"" + articleFields[2] + "\"" + ":" + "\"" + articleInstance[2] + "\"" + ",\n" +
            "\t" + "\"" + articleFields[3] + "\"" + ":" + createDate.getTime() / 1000 + ",\n" +
            "\t" + "\"" + articleFields[4] + "\"" + ":" + editDate.getTime() / 1000 + ",\n" +
            "\t" + "\"" + articleFields[5] + "\"" + ":" + "\"" + articleInstance[5] + "\"" + ",\n" +
            "\t" + "\"" + articleFields[6] + "\"" + ":" + articleInstance[6] + ",\n" +
            "\t" + "\"" + articleFields[7] + "\"" + ":" + articleInstance[7] + ",\n" +
            "\t" + "\"" + articleFields[8] + "\"" + ":" + articleInstance[8] + ",\n" +
            "\t" + "\"" + articleFields[9] + "\"" + ":" + articleInstance[9] + ",\n" +
            "\t" + "\"" + articleFields[10] + "\"" + ":" + "[\n" +
            serializeImagesToJSON(articleImageFields, article.getImages()) +
            "\t" + "],\n" +
            "\t" + "\"" + articleFields[11] + "\"" + ":" + "[\n" +
            serializeTagsIdToJSON(tagFields, article.getTags()) +
            "\t" + "]\n" +
            "}";
    }

    @Override
    public Article toObject() {
        int id = 0;
        String title;
        String lead;
        Timestamp createDate;
        Timestamp editDate;
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
        createDate = new Timestamp(Long.parseLong(m.group(1)));
        indexLine++;
        // editDate
        m = Pattern.compile(":(\\d+),").matcher(lines[indexLine]); // 4/5
        m.find();
        editDate = new Timestamp(Long.parseLong(m.group(1)));
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
        if (Pattern.compile("]").matcher(lines[indexLine + 1]).find()) {
            // если не указаны теги
            if (Pattern.compile("]").matcher(lines[indexLine + 2]).find()) {
                System.out.println("Тегов нет");
            // если теги указаны
            } else {
                int indexEndTag = indexLine + 4;
                while (true) {
                    int idTag = 0;
                    String titleTag;
                    boolean withIdTag;

                    Pattern p0 = Pattern.compile("\"id\":.+");
                    Matcher m0 = p0.matcher(lines[indexEndTag]);
                    withIdTag = m0.find();

                    // id
                    if (withIdTag) {
                        Matcher m1 = Pattern.compile(":(\\d+),").matcher(lines[indexEndTag]);
                        m1.find();
                        idTag = Integer.parseInt(m1.group(1));
                        indexEndTag += 1;
                    }

                    // title
                    Matcher m2 = Pattern.compile(":\"(.+)\"").matcher(lines[indexEndTag]);
                    m2.find();
                    titleTag = m2.group(1);
                    indexEndTag += 2;

                    // создаем тег и добавляем в статью
                    Tag tag;
                    if (withIdTag) {
                        tag = new Tag(idTag, titleTag);
                    } else {
                        tag = new Tag(titleTag);
                    }
                    article.addNewTag(tag);
                    tag.addNewArticle(article);

                    if (Pattern.compile("]").matcher(lines[indexEndTag]).find()) {
                        break;
                    } else {
                        indexEndTag += 1;
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
                boolean withIdImage;

                Pattern p0 = Pattern.compile("\"id\":.+");
                Matcher m0 = p0.matcher(lines[indexEndImages]);
                withIdImage = m0.find();
                //id
                if (withIdImage) {
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
                Matcher m3 = Pattern.compile(":\"(.+)\"").matcher(lines[indexEndImages]);
                m3.find();
                pathImage = m3.group(1);
                indexEndImages += 2;

                // создаем изображение и добавляем в статье
                ArticleImage articleImage;
                if (withIdImage) {
                    articleImage = new ArticleImage(idImage, titleImage, pathImage);
                } else {
                    articleImage = new ArticleImage(titleImage, pathImage);
                }
                article.addNewImage(articleImage);
                articleImage.setArticle(article);

                if (Pattern.compile("]").matcher(lines[indexEndImages]).find()) {
                    indexEndImages += 1;
                    break;
                } else {
                    indexEndImages += 1;
                }
            }

            // если не указаны теги
            if (Pattern.compile("]").matcher(lines[indexEndImages]).find()) {
                System.out.println("Тегов нет");
                // если теги указаны
            } else {
                int indexEndTag = indexEndImages + 2;
                while (true) {
                    int idTag = 0;
                    String titleTag;
                    boolean withIdTag;

                    Pattern p0 = Pattern.compile("\"id\":.+");
                    Matcher m0 = p0.matcher(lines[indexEndTag]);
                    withIdTag = m0.find();

                    // id
                    if (withIdTag) {
                        Matcher m1 = Pattern.compile(":(\\d+),").matcher(lines[indexEndTag]);
                        m1.find();
                        idTag = Integer.parseInt(m1.group(1));
                        indexEndTag += 1;
                    }

                    // title
                    Matcher m2 = Pattern.compile(":\"(.+)\"").matcher(lines[indexEndTag]);
                    m2.find();
                    titleTag = m2.group(1);
                    indexEndTag += 2;

                    // создаем тег и добавляем в статью
                    Tag tag;
                    if (withIdTag) {
                        tag = new Tag(idTag, titleTag);
                    } else {
                        tag = new Tag(titleTag);
                    }
                    article.addNewTag(tag);
                    tag.addNewArticle(article);

                    if (Pattern.compile("]").matcher(lines[indexEndTag]).find()) {
                        break;
                    } else {
                        indexEndTag += 1;
                    }
                }
            }
        }

        return article;
    }

    private String serializeTagsIdToJSON(String[] tagFields, Collection<Tag> tags) {
        List<Object> listTags = Arrays.asList(tags.toArray());
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < listTags.size(); i++) {
            Tag tag = (Tag) listTags.get(i);
            Object[] tagInstance = tag.getObjects();
            String tagString = "" +
                    "\t\t" + "{\n" +
                    "\t\t\t" + "\"" + tagFields[0] + "\"" + ":" + tagInstance[0] + ",\n" +
                    "\t\t\t" + "\"" + tagFields[1] + "\"" + ":" + "\"" + tagInstance[1] + "\"" + "\n" +
                    "\t\t";
            if (i != listTags.size() - 1) {
                tagString += "},\n";
            } else {
                tagString += "}\n";
            }
            result.append(tagString);
        }
        return result.toString();
    }

    private String serializeImagesToJSON(String[] imageFields, Collection<ArticleImage> images) {
        List<Object> listImageObjects = Arrays.asList(images.toArray());
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < listImageObjects.size(); i++) {
            ArticleImage image = (ArticleImage) listImageObjects.get(i);
            Object[] imageInstance = image.getObjects();
            String imageString = "" +
                    "\t\t" + "{\n" +
                    "\t\t\t" + "\"" + imageFields[0] + "\"" + ":" + imageInstance[0] + ",\n" +
                    "\t\t\t" + "\"" + imageFields[1] + "\"" + ":" + "\"" + imageInstance[1] + "\"" + ",\n" +
                    "\t\t\t" + "\"" + imageFields[2] + "\"" + ":" + "\"" + imageInstance[2] + "\"" + "\n" +
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
