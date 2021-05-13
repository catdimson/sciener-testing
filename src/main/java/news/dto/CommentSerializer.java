package news.dto;

import news.model.Comment;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс сериализации комментария в JSON
 */
public class CommentSerializer implements Serializer<Comment> {
    private String json;
    private Comment comment;

    public CommentSerializer(Comment comment) {
        this.comment = comment;
    }

    public CommentSerializer(String json) {
        this.json = json;
    }

    @Override
    public String toJSON() {
        String[] commentFields = Comment.getFields();
        String[] commentAttachmentFields = Comment.CommentAttachment.getFields();
        Object[] commentInstance = comment.getObjects();
        LocalDate createDate = (LocalDate) commentInstance[2];
        LocalDate editDate = (LocalDate) commentInstance[3];

        return "" +
            "{\n" +
            "\t" + "\"" + commentFields[0] + "\"" + ":" + commentInstance[0] + ",\n" +
            "\t" + "\"" + commentFields[1] + "\"" + ":" + "\"" + commentInstance[1] + "\"" + ",\n" +
            "\t" + "\"" + commentFields[2] + "\"" + ":" + Timestamp.valueOf(createDate.atStartOfDay()).getTime() / 1000 + ",\n" +
            "\t" + "\"" + commentFields[3] + "\"" + ":" + Timestamp.valueOf(editDate.atStartOfDay()).getTime() / 1000 + ",\n" +
            "\t" + "\"" + commentFields[4] + "\"" + ":" + commentInstance[4] + ",\n" +
            "\t" + "\"" + commentFields[5] + "\"" + ":" + commentInstance[5] + ",\n" +
            "\t" + "\"" + commentFields[6] + "\"" + ":" + "[\n" +
            serializeAttachmentsToJSON(commentAttachmentFields, commentInstance[6]) +
            "\t" + "]\n" +
            "}";
    }

    @Override
    public Comment toObject() {
        int id = 0;
        String text;
        LocalDate createDate;
        LocalDate editDate;
        int userId;
        int articleId;
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
        // text
        m = Pattern.compile(":\"(.+)\",").matcher(lines[indexLine]);    // 1/2
        m.find();
        text = m.group(1);
        indexLine++;
        // createDate
        m = Pattern.compile(":(\\d+),").matcher(lines[indexLine]);  // 2/3
        m.find();
        int timestampCreateDate = Integer.parseInt(m.group(1));
        createDate = Timestamp.from(Instant.ofEpochSecond(timestampCreateDate)).toLocalDateTime().toLocalDate();
        indexLine++;
        // editDate
        m = Pattern.compile(":(\\d+),").matcher(lines[indexLine]);  // 3/4
        m.find();
        int timestampEditDate = Integer.parseInt(m.group(1));
        editDate = Timestamp.from(Instant.ofEpochSecond(timestampEditDate)).toLocalDateTime().toLocalDate();
        indexLine++;
        // userId
        m = Pattern.compile(":(\\d+),").matcher(lines[indexLine]);  // 4/5
        m.find();
        userId = Integer.parseInt(m.group(1));
        indexLine++;
        // articleId
        m = Pattern.compile(":(\\d+),").matcher(lines[indexLine]);  // 5/6
        m.find();
        articleId = Integer.parseInt(m.group(1));

        // создаем по распарсеным данным объект комментария
        Comment comment;
        if (withId) {
            comment = new Comment(id, text, createDate, editDate, userId, articleId);
        } else {
            comment = new Comment(text, createDate, editDate, userId, articleId);
        }

        // если прикреплений нет
        if (Pattern.compile("]").matcher(lines[indexLine + 2]).find()) {
            System.out.println("Прикреплений нет");
        // если прикрепления есть
        } else {
            int indexEndAttachments = indexLine + 3;
            // извлекаем все прикрепления
            while (true) {
                int idAttachment = 0;
                String titleAttachment;
                String pathAttachment;
                int commentIdAttachment;

                // id
                if (withId) {
                    Matcher m1 = Pattern.compile(":(\\d+),").matcher(lines[indexEndAttachments]);
                    m1.find();
                    idAttachment = Integer.parseInt(m1.group(1));
                    indexEndAttachments += 1;
                }

                // title
                Matcher m2 = Pattern.compile(":\"(.+)\",").matcher(lines[indexEndAttachments]);
                m2.find();
                titleAttachment = m2.group(1);
                indexEndAttachments += 1;

                // path
                Matcher m3 = Pattern.compile(":\"(.+)\",").matcher(lines[indexEndAttachments]);
                m3.find();
                pathAttachment = m3.group(1);
                indexEndAttachments += 1;

                // articleId
                Matcher m4 = Pattern.compile(":(\\d+)").matcher(lines[indexEndAttachments]);
                m4.find();
                commentIdAttachment = Integer.parseInt(m4.group(1));
                indexEndAttachments += 2;

                // создаем пркрепление и добавляем в комментарию
                Comment.CommentAttachment commentAttachment;
                if (withId) {
                    commentAttachment = new Comment.CommentAttachment(idAttachment, titleAttachment, pathAttachment, commentIdAttachment);
                } else {
                    commentAttachment = new Comment.CommentAttachment(titleAttachment, pathAttachment, commentIdAttachment);
                }
                comment.addNewAttachment(commentAttachment);

                if (Pattern.compile("]").matcher(lines[indexEndAttachments]).find()) {
                    break;
                } else {
                    indexEndAttachments += 1;
                }
            }
        }

        return comment;
    }

    private String serializeAttachmentsToJSON(String[] attachmentFields, Object attachments) {
        List listAttachmentObjects = (ArrayList) attachments;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < listAttachmentObjects.size(); i++) {
            Comment.CommentAttachment attachment = (Comment.CommentAttachment) listAttachmentObjects.get(i);
            Object[] attachmentInstance = attachment.getObjects();
            String attachmentString = "" +
                    "\t\t" + "{\n" +
                    "\t\t\t" + "\"" + attachmentFields[0] + "\"" + ":" + attachmentInstance[0] + ",\n" +
                    "\t\t\t" + "\"" + attachmentFields[1] + "\"" + ":" + "\"" + attachmentInstance[1] + "\"" + ",\n" +
                    "\t\t\t" + "\"" + attachmentFields[2] + "\"" + ":" + "\"" + attachmentInstance[2] + "\"" + ",\n" +
                    "\t\t\t" + "\"" + attachmentFields[3] + "\"" + ":" + attachmentInstance[3] + "\n" +
                    "\t\t";
            if (i != listAttachmentObjects.size() - 1) {
                attachmentString += "},\n";
            } else {
                attachmentString += "}\n";
            }
            result.append(attachmentString);
        }
        return result.toString();
    }
}
