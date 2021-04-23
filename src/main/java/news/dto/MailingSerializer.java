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
            "\t" + "\"" + mailingFields[1] + "\"" + ":" + "\"" + mailingInstance[1] + "\"" + ",\n" +
            "}";
    }

    @Override
    public Mailing toObject() {
        int id;
        String email;

        String[] lines = json.split("\n");
        /*for (int i = 0; i < lines.length; i++) {
            System.out.println(i + ":" + lines[i]);
        }*/

        // id
        Pattern p = Pattern.compile(":(\\d+),");
        Matcher m = p.matcher(lines[1]);
        m.find();
        id = Integer.parseInt(m.group(1));
        // email
        m = Pattern.compile(":\"(.+)\",").matcher(lines[2]);
        m.find();
        email = m.group(1);

        // создаем по распарсеным данным объект рассылки
        Mailing mailing = new Mailing(id, email);

        return mailing;
    }
}
