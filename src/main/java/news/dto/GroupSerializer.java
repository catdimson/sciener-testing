package news.dto;

import news.model.Group;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс сериализации группы в JSON
 */
public class GroupSerializer implements Serializer<Group> {
    private String json;
    private Group group;

    public GroupSerializer(Group group) {
        this.group = group;
    }

    public GroupSerializer(String json) {
        this.json = json;
    }

    @Override
    public String toJSON() {
        String[] groupFields = Group.getFields();
        Object[] groupInstance = group.getObjects();

        return "" +
            "{\n" +
            "\t" + "\"" + groupFields[0] + "\"" + ":" + groupInstance[0] + ",\n" +
            "\t" + "\"" + groupFields[1] + "\"" + ":" + "\"" + groupInstance[1] + "\"" + ",\n" +
            "}";
    }

    @Override
    public Group toObject() {
        int id;
        String title;

        String[] lines = json.split("\n");
        /*for (int i = 0; i < lines.length; i++) {
            System.out.println(i + ":" + lines[i]);
        }*/

        // id
        Pattern p = Pattern.compile(":(\\d+),");
        Matcher m = p.matcher(lines[1]);
        m.find();
        id = Integer.parseInt(m.group(1));
        // title
        m = Pattern.compile(":\"(.+)\",").matcher(lines[2]);
        m.find();
        title = m.group(1);

        // создаем по распарсеным данным объект группы
        Group group = new Group(id, title);

        return group;
    }
}
