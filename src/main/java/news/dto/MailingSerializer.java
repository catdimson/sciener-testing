package news.dto;

import news.model.Mailing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс сериализации рассылки в JSON
 */
public class MailingSerializer implements Serializer<Mailing> {
    private String json;
    private Mailing mailing;

    public MailingSerializer(Mailing mailing) {
        this.mailing = mailing;
    }

    public MailingSerializer(String json) {
        this.json = json;
    }

    @Override
    public String toJSON() {
        String[] mailingFields = Mailing.getFields();
        Object[] mailingInstance = mailing.getObjects();

        return "" +
            "{\n" +
            "\t" + "\"" + mailingFields[0] + "\"" + ":" + mailingInstance[0] + ",\n" +
            "\t" + "\"" + mailingFields[1] + "\"" + ":" + "\"" + mailingInstance[1] + "\"" + "\n" +
            "}";
    }

    @Override
    public Mailing toObject() {
        int id = 0;
        String email;
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
        // email
        m = Pattern.compile(":\"(.+)\"").matcher(lines[indexLine]);
        m.find();
        email = m.group(1);

        // создаем по распарсеным данным объект рассылки
        Mailing mailing;
        if (withId) {
            mailing = new Mailing(id, email);
        } else {
            mailing = new Mailing(email);
        }

        return mailing;
    }
}
