package news.web.http;

public interface Response {

    void setStatusCode(int statusNum);

    void setStatusText(String header);

    void setVersion(String version);

    void setHeader(String key, String value);

    String buildResponse();

}

