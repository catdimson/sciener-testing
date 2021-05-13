package news.dto;

import news.model.Source;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс сериализации источника в JSON
 */
public class SourceSerializer implements Serializer<Source> {
    private String json;
    private Source source;

    public SourceSerializer(Source source) {
        this.source = source;
    }

    public SourceSerializer(String json) {
        this.json = json;
    }

    @Override
    public String toJSON() {
        String[] sourceFields = Source.getFields();
        Object[] sourceInstance = source.getObjects();

        return "" +
            "{\n" +
            "\t" + "\"" + sourceFields[0] + "\"" + ":" + sourceInstance[0] + ",\n" +
            "\t" + "\"" + sourceFields[1] + "\"" + ":" + "\"" + sourceInstance[1] + "\"" + ",\n" +
            "\t" + "\"" + sourceFields[2] + "\"" + ":" + "\"" + sourceInstance[2] + "\"" + "\n" +
            "}";
    }

    @Override
    public Source toObject() {
        int id = 0;
        String title;
        String url;
        int indexLine = 1;
        boolean withId;

        String[] lines = json.split("\n");
        /*for (int i = 0; i < lines.length; i++) {
            System.out.println(i + ":" + lines[i]);
        }*/

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
        m = Pattern.compile(":\"(.+)\",").matcher(lines[indexLine]);
        m.find();
        title = m.group(1);
        indexLine++;
        // url
        m = Pattern.compile(":\"(.+)\"").matcher(lines[indexLine]);
        m.find();
        url = m.group(1);

        // создаем по распарсеным данным объект источника
        Source source;
        if (withId) {
            source = new Source(id, title, url);
        } else {
            source = new Source(title, url);
        }

        return source;
    }
}
