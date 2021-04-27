
package news.web.http;

public class HttpResponse implements Response {
    int statusCode;
    String statusText;
    String version;

    @Override
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public void setHeader(String key, String value) {

    }

    @Override
    public String buildResponse() {
        return null;
    }
}

