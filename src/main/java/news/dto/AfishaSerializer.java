package news.dto;

import news.model.Afisha;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
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
        LocalDate date = (LocalDate) afishaInstance[9];

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
            "\t" + "\"" + afishaFields[9] + "\"" + ":" + Timestamp.valueOf(date.atStartOfDay()).getTime() / 1000 + ",\n" +
            "\t" + "\"" + afishaFields[10] + "\"" + ":" + afishaInstance[10] + ",\n" +
            "\t" + "\"" + afishaFields[11] + "\"" + ":" + afishaInstance[11] + ",\n" +
            "\t" + "\"" + afishaFields[12] + "\"" + ":" + afishaInstance[12] + ",\n" +
            "}";
    }

    @Override
    public Afisha toObject() {
        int id;
        String title;
        String imageUrl;
        String lead;
        String description;
        String ageLimit;
        String timing;
        String place;
        String phone;
        LocalDate date;
        boolean isCommercial = false;
        int userId;
        int sourceId;

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
        // imageUrl
        m = Pattern.compile(":\"(.+)\",").matcher(lines[3]);
        m.find();
        imageUrl = m.group(1);
        // lead
        m = Pattern.compile(":\"(.+)\",").matcher(lines[4]);
        m.find();
        lead = m.group(1);
        // description
        m = Pattern.compile(":\"(.+)\",").matcher(lines[5]);
        m.find();
        description = m.group(1);
        // ageLimit
        m = Pattern.compile(":\"(.+)\",").matcher(lines[6]);
        m.find();
        ageLimit = m.group(1);
        // timing
        m = Pattern.compile(":\"(.+)\",").matcher(lines[7]);
        m.find();
        timing = m.group(1);
        // place
        m = Pattern.compile(":\"(.+)\",").matcher(lines[8]);
        m.find();
        place = m.group(1);
        // phone
        m = Pattern.compile(":\"(.+)\",").matcher(lines[9]);
        m.find();
        phone = m.group(1);
        // date
        m = Pattern.compile(":(\\d+),").matcher(lines[10]);
        m.find();
        int timestampCreateDate = Integer.parseInt(m.group(1));
        date = Timestamp.from(Instant.ofEpochSecond(timestampCreateDate)).toLocalDateTime().toLocalDate();
        // isCommercial
        m = Pattern.compile(":(\\w{4,5}),").matcher(lines[11]);
        if (m.find()) {
            if (m.group(1).equals("true")) {
                isCommercial = true;
            }
            if (m.group(1).equals("false")) {
                isCommercial = false;
            }
        }
        // userId
        m = Pattern.compile(":(\\d+),").matcher(lines[12]);
        m.find();
        userId = Integer.parseInt(m.group(1));
        // sourceId
        m = Pattern.compile(":(\\d+),").matcher(lines[13]);
        m.find();
        sourceId = Integer.parseInt(m.group(1));

        // создаем по распарсеным данным объект афиши
        Afisha afisha = new Afisha(id, title, imageUrl, lead, description, ageLimit, timing, place, phone, date, isCommercial,
                userId, sourceId);

        return afisha;
    }
}
