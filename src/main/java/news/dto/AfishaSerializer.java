package news.dto;

import news.model.Afisha;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс сериализации афиши в JSON
 */
public class AfishaSerializer implements Serializer<Afisha> {
    private String json;
    private Afisha afisha;

    public AfishaSerializer(Afisha afisha) {
        this.afisha = afisha;
    }

    public AfishaSerializer(String json) {
        this.json = json;
    }

    @Override
    public String toJSON() {
        String[] afishaFields = Afisha.getFields();
        Object[] afishaInstance = afisha.getObjects();
        Timestamp date = (Timestamp) afishaInstance[9];

        return "" +
            "{\n" +
            "\t" + "\"" + afishaFields[0] + "\"" + ":" + afishaInstance[0] + ",\n" +
            "\t" + "\"" + afishaFields[1] + "\"" + ":" + "\"" + afishaInstance[1] + "\"" + ",\n" +
            "\t" + "\"" + afishaFields[2] + "\"" + ":" + "\"" + afishaInstance[2] + "\"" + ",\n" +
            "\t" + "\"" + afishaFields[3] + "\"" + ":" + "\"" + afishaInstance[3] + "\"" + ",\n" +
            "\t" + "\"" + afishaFields[4] + "\"" + ":" + "\"" + afishaInstance[4] + "\"" + ",\n" +
            "\t" + "\"" + afishaFields[5] + "\"" + ":" + "\"" + afishaInstance[5] + "\"" + ",\n" +
            "\t" + "\"" + afishaFields[6] + "\"" + ":" + "\"" + afishaInstance[6] + "\"" + ",\n" +
            "\t" + "\"" + afishaFields[7] + "\"" + ":" + "\"" + afishaInstance[7] + "\"" + ",\n" +
            "\t" + "\"" + afishaFields[8] + "\"" + ":" + "\"" + afishaInstance[8] + "\"" + ",\n" +
            "\t" + "\"" + afishaFields[9] + "\"" + ":" + date.getTime() / 1000 + ",\n" +
            "\t" + "\"" + afishaFields[10] + "\"" + ":" + afishaInstance[10] + ",\n" +
            "\t" + "\"" + afishaFields[11] + "\"" + ":" + afishaInstance[11] + ",\n" +
            "\t" + "\"" + afishaFields[12] + "\"" + ":" + afishaInstance[12] + "\n" +
            "}";
    }

    @Override
    public Afisha toObject() {
        int id = 0;
        String title;
        String imageUrl;
        String lead;
        String description;
        String ageLimit;
        String timing;
        String place;
        String phone;
        Timestamp date;
        boolean isCommercial = false;
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
        m = Pattern.compile(":\"(.+)\",").matcher(lines[indexLine]);
        m.find();
        title = m.group(1);
        indexLine++;
        // imageUrl
        m = Pattern.compile(":\"(.+)\",").matcher(lines[indexLine]);
        m.find();
        imageUrl = m.group(1);
        indexLine++;
        // lead
        m = Pattern.compile(":\"(.+)\",").matcher(lines[indexLine]);
        m.find();
        lead = m.group(1);
        indexLine++;
        // description
        m = Pattern.compile(":\"(.+)\",").matcher(lines[indexLine]);
        m.find();
        description = m.group(1);
        indexLine++;
        // ageLimit
        m = Pattern.compile(":\"(.+)\",").matcher(lines[indexLine]);
        m.find();
        ageLimit = m.group(1);
        indexLine++;
        // timing
        m = Pattern.compile(":\"(.+)\",").matcher(lines[indexLine]);
        m.find();
        timing = m.group(1);
        indexLine++;
        // place
        m = Pattern.compile(":\"(.+)\",").matcher(lines[indexLine]);
        m.find();
        place = m.group(1);
        indexLine++;
        // phone
        m = Pattern.compile(":\"(.+)\",").matcher(lines[indexLine]);
        m.find();
        phone = m.group(1);
        indexLine++;
        // date
        m = Pattern.compile(":(\\d+),").matcher(lines[indexLine]);
        m.find();
        date = new Timestamp(Long.parseLong(m.group(1)));
        indexLine++;
        // isCommercial
        m = Pattern.compile(":(\\w{4,5}),").matcher(lines[indexLine]);
        if (m.find()) {
            if (m.group(1).equals("true")) {
                isCommercial = true;
            }
            if (m.group(1).equals("false")) {
                isCommercial = false;
            }
        }
        indexLine++;
        // userId
        m = Pattern.compile(":(\\d+),").matcher(lines[indexLine]);
        m.find();
        userId = Integer.parseInt(m.group(1));
        indexLine++;
        // sourceId
        m = Pattern.compile(":(\\d+)").matcher(lines[indexLine]);
        m.find();
        sourceId = Integer.parseInt(m.group(1));

        // создаем по распарсеным данным объект афиши
        Afisha afisha;
        if (withId) {
            afisha = new Afisha(id, title, imageUrl, lead, description, ageLimit, timing, place, phone, date, isCommercial,
                    userId, sourceId);
        } else {
            afisha = new Afisha(title, imageUrl, lead, description, ageLimit, timing, place, phone, date, isCommercial,
                    userId, sourceId);
        }

        return afisha;
    }
}
