package news.dto;

import news.model.Category;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс сериализации категории в JSON
 */
public class CategorySerializer implements Serializer<Category> {
    private String json;
    private Category category;

    public CategorySerializer(Category category) {
        this.category = category;
    }

    public CategorySerializer(String json) {
        this.json = json;
    }

    @Override
    public String toJSON() {
        String[] categoryFields = Category.getFields();
        Object[] categoryInstance = category.getObjects();

        return "" +
            "{\n" +
            "\t" + "\"" + categoryFields[0] + "\"" + ":" + categoryInstance[0] + ",\n" +
            "\t" + "\"" + categoryFields[1] + "\"" + ":" + "\"" + categoryInstance[1] + "\"" + ",\n" +
            "}";
    }

    @Override
    public Category toObject() {
        int id;
        String title;
        Category category;

        String[] lines = json.split("\n");

        // id
        Pattern p = Pattern.compile(":(\\d+),");
        Matcher m = p.matcher(lines[1]);
        if (m.find()) {
            id = Integer.parseInt(m.group(1));
            // title
            m = Pattern.compile(":\"(.+)\",").matcher(lines[2]);
            m.find();
            title = m.group(1);
            category = new Category(id, title);
        } else {
            // title
            m = Pattern.compile(":\"(.+)\",").matcher(lines[1]);
            m.find();
            title = m.group(1);
            category = new Category(title);
        }

        return category;
    }
}
