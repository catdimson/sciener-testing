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
            "\t" + "\"" + sourceFields[2] + "\"" + ":" + "\"" + sourceInstance[2] + "\"" + ",\n" +
            "}";
    }

    @Override
    public Source toObject() {
        int id;
        String title;
        String url;

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
        // url
        m = Pattern.compile(":\"(.+)\",").matcher(lines[3]);
        m.find();
        url = m.group(1);

        // создаем по распарсеным данным объект источника
        Source source = new Source(id, title, url);

        return source;
    }
}
