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
            "\t" + "\"" + groupFields[1] + "\"" + ":" + "\"" + groupInstance[1] + "\"" + "\n" +
            "}";
    }

    @Override
    public Group toObject() {
        int id = 0;
        String title;
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
        m = Pattern.compile(":\"(.+)\"").matcher(lines[indexLine]);
        m.find();
        title = m.group(1);
        Group group;
        if (withId) {
            group = new Group(id, title);
        } else {
            group = new Group(title);
        }

        return group;
    }
}
