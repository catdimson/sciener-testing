package news.dto;

import news.model.Tag;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс сериализации тега в JSON
 */
public class TagSerializer implements Serializer<Tag> {
    private String json;
    private Tag tag;

    public TagSerializer(Tag tag) {
        this.tag = tag;
    }

    public TagSerializer(String json) {
        this.json = json;
    }

    @Override
    public String toJSON() {
        String[] tagFields = Tag.getFields();
        Object[] tagInstance = tag.getObjects();

        return "" +
            "{\n" +
            "\t" + "\"" + tagFields[0] + "\"" + ":" + tagInstance[0] + ",\n" +
            "\t" + "\"" + tagFields[1] + "\"" + ":" + "\"" + tagInstance[1] + "\"" + "\n" +
            "}";
    }

    @Override
    public Tag toObject() {
        int id = 0;
        String title;
        int indexLine = 1;
        boolean withId;

        String[] lines = json.split("\n");
        /*for (int i = 0; i < lines.length; i++) {
            System.out.println(i + ":" + lines[i]);
        }*/
        System.out.println("json: \n" + json);

        // id
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

        // создаем по распарсеным данным объект тега
        Tag tag;
        if (withId) {
            tag = new Tag(id, title);
        } else {
            tag = new Tag(title);
        }

        return tag;
    }
}
