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
            "\t" + "\"" + categoryFields[1] + "\"" + ":" + "\"" + categoryInstance[1] + "\"" + "\n" +
            "}";
    }

    @Override
    public Category toObject() {
        int id = 0;
        String title;
        Category category;
        int indexLine = 1;
        boolean withId;

        String[] lines = json.split("\n");
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
        m = Pattern.compile(":\"(.+)\"").matcher(lines[indexLine]);
        m.find();
        title = m.group(1);
        category = new Category(id, title);

        return category;
    }
}
