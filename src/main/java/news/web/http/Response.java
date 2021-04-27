package news.web.http;

public interface Response {

    void setStatusCode(int statusCode);

    void setStatusText(String statusText);

    void setVersion(String version);

    void setHeader(String key, String value);

    String getResponse();

}

